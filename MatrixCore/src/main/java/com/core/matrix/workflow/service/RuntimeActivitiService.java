/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.workflow.service;

import com.core.matrix.request.CompleteTaskRequest;
import com.core.matrix.request.StartProcessRequest;
import com.core.matrix.response.ProcessDefinitionResponse;
import com.core.matrix.response.TaskResponse;
import com.core.matrix.utils.Utils;
import com.core.matrix.workflow.model.GroupMemberActiviti;
import com.core.matrix.workflow.model.UserActiviti;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.activiti.engine.ActivitiTaskAlreadyClaimedException;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.bpmn.behavior.ExclusiveGatewayActivityBehavior;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.task.TaskDefinition;
import org.activiti.engine.repository.ProcessDefinition;
//import org.activiti.engine.impl.util.ProcessDefinitionUtil;

import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.DelegationState;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

/**
 *
 * @author thiag
 */
@Service
public class RuntimeActivitiService {

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private ApplicationContext context;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private UserActivitiService userActivitiService;

    @Autowired
    private RepositoryActivitiService repositoryActivitiService;

    public Optional<TaskResponse> startProcess(StartProcessRequest request, String userId) throws Exception {

        request.getVariables().put("created_by", userId);
        request.getVariables().put("created_at", Utils.dateTimeNowFormated());

        ProcessInstance processInstance = this.runtimeService.startProcessInstanceByKey(request.getKey(), request.getVariables());

        Task task = this.getNextUserTaskByProcessInstanceId(processInstance.getId(), userId, true);

        return Optional.ofNullable(task).isPresent() ? Optional.of(new TaskResponse(task, context)) : Optional.empty();

    }

    public Optional<TaskResponse> completeTask(String userId, CompleteTaskRequest request) throws Exception {

        Task currentTask = taskService.createTaskQuery().taskId(request.getTaskId()).singleResult();
        boolean delegated = false;
        String owner = userId;

        if (currentTask.getDelegationState() != null && currentTask.getDelegationState().equals(DelegationState.PENDING)) {
            owner = currentTask.getOwner();
            delegated = true;
            taskService.resolveTask(request.getTaskId(), request.getVariables());
            taskService.complete(request.getTaskId());
        } else {
            taskService.complete(request.getTaskId(), request.getVariables());
        }
        Task nextTask = getNextUserTaskByProcessInstanceId(request.getProcessInstanceId(), owner, true);

        if (nextTask == null) {
            //envia email para o proximo grupo
            //sendEmailsForGroups(processInstanceId);
        } else {
            nextTask.setAssignee(owner);
        }

        //a proxima tarefa eh de quem delegou
        return delegated && !userId.equals(owner) ? Optional.empty() : Optional.ofNullable(new TaskResponse(nextTask, context));

    }

    public TaskResponse getTask(String taskId) {

        TaskInfo t = taskService.createTaskQuery().active().taskId(taskId).includeProcessVariables().includeTaskLocalVariables().singleResult();
        if (t == null) {
            t = historyService.createHistoricTaskInstanceQuery().taskId(taskId).includeProcessVariables().includeTaskLocalVariables().singleResult();
        }
        if (t != null && t instanceof Task && !t.getTaskLocalVariables().containsKey("groups")) {
            generateQuestionBar(t);
            t = historyService.createHistoricTaskInstanceQuery().taskId(taskId).includeProcessVariables().includeTaskLocalVariables().singleResult();
        }

        return new TaskResponse(t, context);
    }

    public Map<String, Object> getLocalVariables(String taskId) {
        return taskService.getVariablesLocal(taskId);
    }

    public Map<String, Object> getVariables(String processInstanceId) {
        return runtimeService.getVariables(processInstanceId);
    }

    public void assigneeTask(String taskId, String userId) {

        if (!Optional.ofNullable(userId).isPresent()) {
            userId = null;
        }

        try {
            taskService.claim(taskId, userId);
        } catch (ActivitiTaskAlreadyClaimedException e) {
            taskService.setAssignee(taskId, userId);
        }

    }

    public List<TaskResponse> getGroupTasks(String userId) {

        return taskService.createNativeTaskQuery().sql("SELECT \n"
                + "    *\n"
                + "FROM\n"
                + "    ACT_RU_TASK t\n"
                + "        INNER JOIN\n"
                + "    ACT_RU_IDENTITYLINK i ON t.id_ = i.task_id_\n"
                + "WHERE\n"
                + "    assignee_ IS NULL\n"
                + "        AND group_id_ IN (SELECT \n"
                + "            a.GROUP_ID_\n"
                + "        FROM\n"
                + "            act_id_membership a\n"
                + "                INNER JOIN\n"
                + "            act_id_user b ON a.USER_ID_ = b.ID_ where  b.id_ = '" + userId + "')")
                .list()
                .stream()
                .map((t) -> new TaskResponse(t, context))
                .collect(Collectors.toList());

    }

    public List<TaskResponse> getCandidateTasks(String user) {

        List<ProcessDefinitionResponse> processDefinitions = Collections.synchronizedList(repositoryActivitiService.listAll());

        return taskService
                .createTaskQuery()
                .taskCandidateUser(user)
                .includeProcessVariables()
                .includeTaskLocalVariables()
                .list()
                .parallelStream()
                .map(t -> {
                    TaskResponse instance = new TaskResponse(t, context);
                    Optional<ProcessDefinitionResponse> response = processDefinitions.stream().filter(p -> p.getId().equals(t.getProcessDefinitionId())).findFirst();
                    String name = response.isPresent() ? response.get().getName() : "";
                    instance.setProcessDefinitionName(name);
                    return instance;
                })
                .collect(Collectors.toList());

    }

    public List<TaskResponse> getMyTasks(String user, int page, int size) {
        
        List<ProcessDefinitionResponse> processDefinitions = Collections.synchronizedList(repositoryActivitiService.listAll());
        
        return taskService
                .createTaskQuery()
                .taskAssignee(user)
                .includeProcessVariables()
                .listPage(page, size)
                .parallelStream()
                .map(t -> {
                    TaskResponse instance = new TaskResponse(t, context);
                    Optional<ProcessDefinitionResponse> response = processDefinitions.stream().filter(p -> p.getId().equals(t.getProcessDefinitionId())).findFirst();
                    String name = response.isPresent() ? response.get().getName() : "";
                    instance.setProcessDefinitionName(name);
                    return instance;
                })
                .collect(Collectors.toList());

    }

    public List<TaskResponse> getInvolvedTasks(String user) {

        return taskService
                .createTaskQuery()
                .taskInvolvedUser(user)
                .includeProcessVariables()
                .list()
                .parallelStream()
                .map(t -> new TaskResponse(t, context))
                .collect(Collectors.toList());

    }

    public Task getNextUserTaskByProcessInstanceId(String processInstanceId, String userId, boolean assigneToUser) throws Exception {

        List<Task> l = taskService.createTaskQuery().active().taskCandidateOrAssigned(userId).processInstanceId(processInstanceId).includeProcessVariables().includeTaskLocalVariables().list();

        if (l != null && !l.isEmpty()) {
            if (assigneToUser) {
                taskService.claim(l.get(0).getId(), userId);
            }

            generateQuestionBar(l.get(0));
            return l.get(0);
        }

        // Find by Group
        GroupMemberActiviti group = userActivitiService
                .findById(userId)
                .getGroups()
                .stream()
                .findFirst()
                .orElseThrow(() -> new Exception("User not associate a group."));

        l = taskService.createTaskQuery().active().taskCandidateGroup(group.getGroupId())
                .processInstanceId(processInstanceId)
                .includeProcessVariables()
                .includeTaskLocalVariables()
                .list();

        if (l != null && !l.isEmpty()) {
            if (assigneToUser) {
                l.get(0).setAssignee(userId);
                taskService.claim(l.get(0).getId(), userId);
            }

            generateQuestionBar(l.get(0));
            return l.get(0);
        }

        //Procura proxima tarafa nas subtasks
        List<ProcessInstance> subprocessList = runtimeService.createProcessInstanceQuery().superProcessInstanceId(processInstanceId).list();
        for (ProcessInstance pi : subprocessList) {
            List<Task> lt = taskService.createTaskQuery().active().taskCandidateOrAssigned(userId).processInstanceId(pi.getProcessInstanceId()).includeProcessVariables().includeTaskLocalVariables().list();
            if (lt != null && !lt.isEmpty()) {
                if (assigneToUser) {
                    taskService.claim(lt.get(0).getId(), userId);
                }

                generateQuestionBar(lt.get(0));
                return lt.get(0);
            }
        }

        //Procura tarefa no ProcessInstance pai
        List<HistoricProcessInstance> hpi = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).list();
        if (hpi != null && !hpi.isEmpty() && hpi.get(0).getSuperProcessInstanceId() != null) {
            l = taskService.createTaskQuery().active().taskCandidateOrAssigned(userId).processInstanceId(hpi.get(0).getSuperProcessInstanceId()).includeProcessVariables().includeTaskLocalVariables().list();
            if (l != null && !l.isEmpty()) {
                if (assigneToUser) {
                    taskService.claim(l.get(0).getId(), userId);
                }

                generateQuestionBar(l.get(0));
                return l.get(0);
            }
        }

        return null;

    }

    public void generateQuestionBar(TaskInfo t) {

        List<Execution> executions = this.runtimeService.createExecutionQuery()
                .executionId(t.getExecutionId())
                .list();

        Execution execution = executions.get(0);
        ExecutionEntity executionEntity = (ExecutionEntity) execution;

        ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) ((RepositoryServiceImpl) this.repositoryService)
                .getDeployedProcessDefinition(t.getProcessDefinitionId());

        //atividade atual
        ActivityImpl activity = processDefinitionEntity.findActivity(executionEntity.getActivityId());

        TaskDefinition td = (TaskDefinition) activity.getProperty("taskDefinition");
        List<String> groupList = new ArrayList<>();
        for (Expression e : td.getCandidateGroupIdExpressions()) {
            groupList.add("'" + e.getExpressionText() + "'");
        }
        //variavel que indica os grupos que podem realizar a tarefa
        taskService.setVariableLocal(t.getId(), "groups", groupList.toString());

        //lista das proximas transacoes
        List<PvmTransition> pvmTransitionList = activity.getOutgoingTransitions();
        if (pvmTransitionList != null) {
            for (PvmTransition pvm : pvmTransitionList) {
                //Proxima atividade
                ActivityImpl gat = (ActivityImpl) pvm.getDestination();
                //se for um exclusive Gateway
                if (gat != null && gat.getActivityBehavior() instanceof ExclusiveGatewayActivityBehavior) {
                    //lista das transacoes do gateway
                    List<PvmTransition> decisions = gat.getOutgoingTransitions();
                    //cria variavel local question
                    taskService.setVariableLocal(t.getId(), "question", gat.getProperty("name"));
                    List<String> answers = new ArrayList();
                    String variable = decisions.get(0).getProperty("conditionText").toString();
                    //cria variavel local 'variable' que sera usado na decisao
                    taskService.setVariableLocal(t.getId(), "variable", variable.substring(variable.indexOf('{') + 1, variable.indexOf('=')));
                    for (PvmTransition dec : decisions) {
                        if (dec.getProperty("documentation") != null) {
                            answers.add(dec.getProperty("documentation").toString());
                        }
                    }
                    //cria variavel local 'answers' que serão as possiveis respostas
                    taskService.setVariableLocal(t.getId(), "answers", String.join(",", answers));
                }
            }
        }
    }

}
