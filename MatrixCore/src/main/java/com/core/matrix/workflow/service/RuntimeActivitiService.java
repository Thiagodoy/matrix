/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.workflow.service;

import com.core.matrix.request.AddComment;
import com.core.matrix.request.CompleteTaskRequest;
import com.core.matrix.request.DeleteProcessRequest;
import com.core.matrix.request.StartProcessRequest;
import com.core.matrix.response.AttachmentResponse;
import com.core.matrix.response.PageResponse;
import com.core.matrix.response.ProcessDefinitionResponse;
import com.core.matrix.response.ProcessDetailResponse;
import com.core.matrix.response.TaskResponse;
import com.core.matrix.utils.Constants;
import com.core.matrix.utils.Utils;
import com.core.matrix.workflow.model.GroupMemberActiviti;
import com.core.matrix.workflow.model.UserActiviti;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
//import org.activiti.engine.impl.util.ProcessDefinitionUtil;

import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Attachment;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.DelegationState;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
    private HistoryService historyService;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private UserActivitiService userActivitiService;

    @Autowired
    private RepositoryActivitiService repositoryActivitiService;

    @Autowired
    private CommentActivitiService commentActivitiService;

    @Autowired
    private GroupActivitiService groupActivitiService;

    @Transactional
    public void startProcessByMessage(String message, Map<String, Object> variables) {
        this.runtimeService.startProcessInstanceByMessage(message, variables);
    }

    @Transactional
    public String startProcessByMessage(String message) {
        return runtimeService.startProcessInstanceByMessage(message).getProcessInstanceId();
    }

    @Transactional
    public Optional<TaskResponse> startProcess(StartProcessRequest request, String userId) throws Exception {

        if (!Optional.ofNullable(request.getVariables()).isPresent()) {
            request.setVariables(new HashMap<String, Object>());
        }

        request.getVariables().put(Constants.CREATED_BY, userId);
        request.getVariables().put(Constants.CREATED_AT, Utils.dateTimeNowFormated());
        //request.getVariables().put("taskSe", taskService);
        ProcessInstance processInstance = this.runtimeService.startProcessInstanceByKey(request.getKey(), request.getVariables());

        Task task = this.getNextUserTaskByProcessInstanceId(processInstance.getId(), userId, true);

        TaskResponse taskResponse = new TaskResponse(task);
        Optional<ProcessDefinitionResponse> resp = repositoryActivitiService
                .listAll()
                .stream()
                .filter(p -> p.getId().equals(task.getProcessDefinitionId()))
                .findFirst();

        String name = resp.isPresent() ? resp.get().getName() : "";
        taskResponse.setProcessDefinitionName(name);

        return Optional.ofNullable(task).isPresent() ? Optional.of(taskResponse) : Optional.empty();

    }

    @Transactional
    public Optional<TaskResponse> completeTask(String userId, CompleteTaskRequest request) throws Exception {

        Task currentTask = taskService.createTaskQuery().taskId(request.getTaskId()).singleResult();
        boolean delegated = false;
        String owner = userId;

        if (currentTask != null && currentTask.getDelegationState() != null && currentTask.getDelegationState().equals(DelegationState.PENDING)) {
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

        if (nextTask != null) {
            TaskResponse taskResponse = new TaskResponse(nextTask);
            Optional<ProcessDefinitionResponse> resp = repositoryActivitiService
                    .listAll()
                    .stream()
                    .filter(p -> p.getId().equals(nextTask.getProcessDefinitionId()))
                    .findFirst();

            String name = resp.isPresent() ? resp.get().getName() : "";
            taskResponse.setProcessDefinitionName(name);

            return delegated && !userId.equals(owner) ? Optional.empty() : Optional.ofNullable(new TaskResponse(nextTask));
        }

        return Optional.empty();
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

        if (t == null) {
            return null;
        }

        TaskResponse task = new TaskResponse(t);
        Optional<ProcessDefinitionResponse> resp = repositoryActivitiService
                .listAll()
                .stream()
                .filter(p -> p.getId().equals(task.getProcessDefinitionId()))
                .findFirst();

        String name = resp.isPresent() ? resp.get().getName() : "";
        task.setProcessDefinitionName(name);

        return task;
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
                .parallelStream()
                .map(t -> {
                    TaskResponse instance = new TaskResponse(t);
                    Optional<ProcessDefinitionResponse> resp = repositoryActivitiService.listAll().stream().filter(p -> p.getId().equals(t.getProcessDefinitionId())).findFirst();
                    String name = resp.isPresent() ? resp.get().getName() : "";
                    instance.setProcessDefinitionName(name);
                    return instance;
                })
                .collect(Collectors.toList());

    }

    public PageResponse<TaskResponse> getCandidateTasks(UserActiviti user, String variableValue, String processInstance, int page, int size) {

        // grou
        int min = page * size;
        //int max = min + size;

        if (Optional.ofNullable(variableValue).isPresent()) {

            String gro = user.getGroups().stream().map(g -> "'" + g.getGroupId() + "'").filter(Objects::nonNull).collect(Collectors.joining(","));

            List<String> processInstances = taskService
                    .createNativeTaskQuery().sql("SELECT \n"
                            + " distinct   t.PROC_INST_ID_,\n"
                            + "    t.ID_ \n"
                            + "FROM\n"
                            + "    activiti.act_ru_task t\n"
                            + "        INNER JOIN\n"
                            + "    activiti.act_ru_variable v ON t.PROC_INST_ID_ = v.PROC_INST_ID_\n"
                            + "        INNER JOIN\n"
                            + "    activiti.ACT_RU_IDENTITYLINK k ON t.ID_ = k.TASK_ID_\n"
                            + "WHERE\n"
                            + "    upper(v.TEXT_) LIKE '%" + variableValue.toUpperCase() + "%'\n"
                            + "        AND k.group_id_ in(" + gro + ") ")
                    .list()
                    .stream()
                    .map(t -> t.getProcessInstanceId())
                    .distinct()
                    .collect(Collectors.toList());

            if (processInstances.isEmpty()) {
                return new PageResponse<TaskResponse>(new ArrayList(), 1L, 1L, (long) page);
            }

            List<TaskResponse> response = taskService
                    .createTaskQuery()
                    .includeProcessVariables()
                    .includeTaskLocalVariables()
                    .processInstanceIdIn(processInstances)
                    .orderByTaskCreateTime()
                    .desc()
                    .listPage(min, size)
                    .parallelStream()
                    .map(t -> {
                        TaskResponse instance = new TaskResponse(t);
                        Optional<ProcessDefinitionResponse> resp = repositoryActivitiService.listAll().stream().filter(p -> p.getId().equals(t.getProcessDefinitionId())).findFirst();
                        String name = resp.isPresent() ? resp.get().getName() : "";
                        instance.setProcessDefinitionName(name);
                        return instance;
                    })
                    .collect(Collectors.toList());

            return new PageResponse<TaskResponse>(response, (long) processInstances.size(), (long) size, (long) page);

        } else if (Optional.ofNullable(processInstance).isPresent()) {

            List<TaskResponse> response = taskService
                    .createTaskQuery()
                    //.taskCandidateGroupIn(groups)
                    .processInstanceId(processInstance)
                    .includeProcessVariables()
                    .includeTaskLocalVariables()
                    .orderByTaskCreateTime()
                    .desc()
                    .listPage(min, size)
                    .parallelStream()
                    .map(t -> {
                        TaskResponse instance = new TaskResponse(t);
                        Optional<ProcessDefinitionResponse> resp = repositoryActivitiService.listAll().stream().filter(p -> p.getId().equals(t.getProcessDefinitionId())).findFirst();
                        String name = resp.isPresent() ? resp.get().getName() : "";
                        instance.setProcessDefinitionName(name);
                        return instance;
                    })
                    .collect(Collectors.toList());

            response.stream().forEach(task -> {
                //esta apresentando erro para listar.. não reconhece 
//            List<String> gr = groupActivitiService.listByTask(task.getTaskId()).stream().map(g -> g.getName()).collect(Collectors.toList());
//            task.setProfileCanditates(gr);
            });

            return new PageResponse<TaskResponse>(response, 1L, 1L, (long) page);

        } else {

            List<String> groups = user.getGroups().stream().map(g -> g.getGroupId()).filter(Objects::nonNull).collect(Collectors.toList());
            Long sizeTotalElements = taskService.createTaskQuery().taskCandidateGroupIn(groups).count();

            List<TaskResponse> response = taskService
                    .createTaskQuery()
                    .taskCandidateGroupIn(groups)
                    .includeProcessVariables()
                    .includeTaskLocalVariables()
                    .orderByTaskCreateTime()
                    .desc()
                    .listPage(min, size)
                    .parallelStream()
                    .map(t -> {
                        TaskResponse instance = new TaskResponse(t);
                        Optional<ProcessDefinitionResponse> resp = repositoryActivitiService.listAll().stream().filter(p -> p.getId().equals(t.getProcessDefinitionId())).findFirst();
                        String name = resp.isPresent() ? resp.get().getName() : "";
                        instance.setProcessDefinitionName(name);
                        return instance;
                    })
                    .collect(Collectors.toList());

            response.stream().forEach(task -> {
                //esta apresentando erro para listar.. não reconhece 
//            List<String> gr = groupActivitiService.listByTask(task.getTaskId()).stream().map(g -> g.getName()).collect(Collectors.toList());
//            task.setProfileCanditates(gr);
            });

            return new PageResponse<TaskResponse>(response, sizeTotalElements, (long) size, (long) page);
        }

    }

    public PageResponse<TaskResponse> getCandidateTasks(UserActiviti user, int page, int size) {

        List<String> groups = user.getGroups().stream().map(g -> g.getGroupId()).filter(Objects::nonNull).collect(Collectors.toList());

        Long sizeTotalElements = taskService.createTaskQuery().taskCandidateGroupIn(groups).count();
        // grou

        int min = page * size;
        //int max = min + size;

        List<TaskResponse> response = taskService
                .createTaskQuery()
                .taskCandidateGroupIn(groups)
                .includeProcessVariables()
                .includeTaskLocalVariables()
                .orderByTaskCreateTime()
                .desc()
                .listPage(min, size)
                .parallelStream()
                .map(t -> {
                    TaskResponse instance = new TaskResponse(t);
                    Optional<ProcessDefinitionResponse> resp = repositoryActivitiService.listAll().stream().filter(p -> p.getId().equals(t.getProcessDefinitionId())).findFirst();
                    String name = resp.isPresent() ? resp.get().getName() : "";
                    instance.setProcessDefinitionName(name);
                    return instance;
                })
                .collect(Collectors.toList());

        response.stream().forEach(task -> {
            //esta apresentando erro para listar.. não reconhece 
//            List<String> gr = groupActivitiService.listByTask(task.getTaskId()).stream().map(g -> g.getName()).collect(Collectors.toList());
//            task.setProfileCanditates(gr);
        });

        return new PageResponse<TaskResponse>(response, sizeTotalElements, (long) size, (long) page);

    }

    @Transactional(readOnly = true)
    public PageResponse<TaskResponse> getMyTasks(String user, String variableValue, String processInstance, int page, int size) {

        int min = page * size;

        if (Optional.ofNullable(variableValue).isPresent()) {

            List<String> processInstances = taskService
                    .createNativeTaskQuery().sql("SELECT \n"
                            + "    distinct t.PROC_INST_ID_,\n"
                            + "    t.ID_ \n"
                            + "FROM\n"
                            + "    activiti.act_ru_task t\n"
                            + "        INNER JOIN\n"
                            + "    activiti.act_ru_variable v ON t.PROC_INST_ID_ = v.PROC_INST_ID_\n"
                            + "        INNER JOIN\n"
                            + "    activiti.ACT_RU_IDENTITYLINK k ON t.PROC_INST_ID_ = k.PROC_INST_ID_\n"
                            + "WHERE\n"
                            + "    upper(v.TEXT_) LIKE '%" + variableValue.toUpperCase() + "%'\n"
                            + "        AND k.user_id_ = '" + user + "'; ").list()
                    .stream()
                    .map(t -> t.getProcessInstanceId())
                    .distinct()
                    .collect(Collectors.toList());

            if (processInstances.isEmpty()) {
                return new PageResponse<TaskResponse>(new ArrayList(), 1L, 1L, (long) page);
            }

            List<TaskResponse> response = taskService
                    .createTaskQuery()
                    .includeProcessVariables()
                    .includeTaskLocalVariables()
                    .processInstanceIdIn(processInstances)
                    .orderByTaskCreateTime()
                    .desc()
                    .listPage(min, size)
                    .parallelStream()
                    .map(t -> {
                        TaskResponse instance = new TaskResponse(t);
                        Optional<ProcessDefinitionResponse> resp = repositoryActivitiService.listAll().stream().filter(p -> p.getId().equals(t.getProcessDefinitionId())).findFirst();
                        String name = resp.isPresent() ? resp.get().getName() : "";
                        instance.setProcessDefinitionName(name);
                        return instance;
                    })
                    .collect(Collectors.toList());

            return new PageResponse<TaskResponse>(response, (long) processInstances.size(), (long) size, (long) page);

            // return new PageResponse<TaskResponse>(response, (long) response.size(), (long) processInstances.size(), (long) page);
        } else if (Optional.ofNullable(processInstance).isPresent()) {

            List<TaskResponse> response = taskService
                    .createTaskQuery()
                    .taskAssignee(user)
                    .processInstanceId(processInstance)
                    .includeProcessVariables()
                    .includeTaskLocalVariables()
                    .orderByTaskCreateTime()
                    .desc()
                    .listPage(min, size)
                    .parallelStream()
                    .map(t -> {
                        TaskResponse instance = new TaskResponse(t);
                        Optional<ProcessDefinitionResponse> resp = repositoryActivitiService.listAll().stream().filter(p -> p.getId().equals(t.getProcessDefinitionId())).findFirst();
                        String name = resp.isPresent() ? resp.get().getName() : "";
                        instance.setProcessDefinitionName(name);
                        return instance;
                    })
                    .collect(Collectors.toList());

            response.stream().forEach(task -> {
                //esta apresentando erro para listar.. não reconhece 
//            List<String> gr = groupActivitiService.listByTask(task.getTaskId()).stream().map(g -> g.getName()).collect(Collectors.toList());
//            task.setProfileCanditates(gr);
            });

            return new PageResponse<TaskResponse>(response, 1L, 1L, (long) page);

        } else {

            Long sizeTotalElements = taskService.createTaskQuery().taskAssignee(user).count();

            List<TaskResponse> response = taskService
                    .createTaskQuery()
                    .taskAssignee(user)
                    .includeProcessVariables()
                    .includeTaskLocalVariables()
                    .orderByTaskCreateTime()
                    .desc()
                    .listPage(min, size)
                    .parallelStream()
                    .map(t -> {
                        TaskResponse instance = new TaskResponse(t);
                        Optional<ProcessDefinitionResponse> resp = repositoryActivitiService.listAll().stream().filter(p -> p.getId().equals(t.getProcessDefinitionId())).findFirst();
                        String name = resp.isPresent() ? resp.get().getName() : "";
                        instance.setProcessDefinitionName(name);
                        return instance;
                    })
                    .collect(Collectors.toList());

            response.stream().forEach(task -> {
                //esta apresentando erro para listar.. não reconhece 
//            List<String> gr = groupActivitiService.listByTask(task.getTaskId()).stream().map(g -> g.getName()).collect(Collectors.toList());
//            task.setProfileCanditates(gr);
            });

            return new PageResponse<TaskResponse>(response, (long) sizeTotalElements, (long) size, (long) page);
        }

//        List<TaskResponse> response = taskService
//                .createTaskQuery()
//                .taskAssignee(user)
//                .includeProcessVariables()
//                .orderByTaskCreateTime()
//                .desc()
//                .listPage(min, size)
//                .parallelStream()
//                .map(t -> {
//                    TaskResponse instance = new TaskResponse(t);
//
//                    Optional<ProcessDefinitionResponse> resp = repositoryActivitiService.listAll().stream().filter(p -> p.getKey().equals(instance.getKey())).findFirst();
//                    String name = resp.isPresent() ? resp.get().getName() : "";
//                    instance.setProcessDefinitionName(name);
//                    return instance;
//                })
//                .collect(Collectors.toList());
//
//        return new PageResponse<TaskResponse>(response, (long) response.size(), sizeTotalElements, (long) page);
    }

    @Transactional(readOnly = true)
    public PageResponse<TaskResponse> getInvolvedTasks(String user, int page, int size) {

        Long sizeTotalElements = taskService.createTaskQuery().taskInvolvedUser(user).count();

        List<TaskResponse> response = taskService
                .createTaskQuery()
                .taskInvolvedUser(user)
                .includeProcessVariables()
                .orderByTaskCreateTime()
                .desc()
                .listPage(page, size)
                .parallelStream()
                .map(t -> {
                    TaskResponse instance = new TaskResponse(t);
                    Optional<ProcessDefinitionResponse> resp = repositoryActivitiService.listAll().stream().filter(p -> p.getId().equals(t.getProcessDefinitionId())).findFirst();
                    String name = resp.isPresent() ? resp.get().getName() : "";
                    instance.setProcessDefinitionName(name);
                    return instance;
                })
                .collect(Collectors.toList());

        return new PageResponse<TaskResponse>(response, (long) sizeTotalElements, (long) size, (long) page);

    }

    @Transactional
    public Task getNextUserTaskByProcessInstanceId(String processInstanceId, String userId, boolean assigneToUser) throws Exception {

        List<Task> l = taskService
                .createTaskQuery()
                .active()
                .taskCandidateOrAssigned(userId)
                .processInstanceId(processInstanceId)
                .includeProcessVariables()
                .includeTaskLocalVariables()
                .list();

        if (l != null && !l.isEmpty()) {
            if (assigneToUser) {
                taskService.setAssignee(l.get(0).getId(), userId);
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
        taskService.setVariableLocal(t.getId(), "groups", groupList);

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

    public ProcessDetailResponse getDetail(String processInstanceId) {

        ProcessDetailResponse detail = new ProcessDetailResponse();

        List<Comment> comments = taskService
                .getProcessInstanceComments(processInstanceId)
                .stream()
                .sorted(Comparator.comparing(Comment::getTime).reversed())
                .collect(Collectors.toList());

        detail.setComments(comments);
        List<AttachmentResponse> attachmentResponses = taskService.getProcessInstanceAttachments(processInstanceId).parallelStream().map(a -> new AttachmentResponse(a)).collect(Collectors.toList());
        detail.setAttachments(attachmentResponses);

        //HistoricProcessInstance instance = historyService.createHistoricProcessInstanceQuery().processDefinitionId(processInstanceId).singleResult();
        // detail.setProcessCreatedDate(instance.getStartTime());
        //detail.setProcessCreatedUser(instance.getStartUserId());
        return detail;

    }

    @Transactional
    public Comment addComment(AddComment request, String user) {

        Comment comment = taskService.addComment(null, request.getProcessInstanceId(), request.getMessage());

        commentActivitiService.setUser(comment.getId(), user);
        comment = taskService.getComment(comment.getId());
        return comment;
    }

    @Transactional
    public void deleteComment(String id) {
        taskService.deleteComment(id);
    }

    @Transactional
    public List<AttachmentResponse> createAttachament(String processInstance, MultipartFile[] files) throws IOException {

        List<AttachmentResponse> responses = new ArrayList<>();

        for (MultipartFile file : files) {
            Attachment attachment = taskService
                    .createAttachment(file.getContentType(), null, processInstance, file.getOriginalFilename(), "attachmentDescription", file.getInputStream());
            responses.add(new AttachmentResponse(attachment));
        }

        return responses;
    }

    @Transactional(readOnly = true)
    public InputStream getAttachamentContent(String attachamentId) {
        return taskService.getAttachmentContent(attachamentId);
    }

    @Transactional(readOnly = true)
    public Attachment getAttachament(String attachamentId) {
        return taskService.getAttachment(attachamentId);
    }

    @Transactional
    public void deleteAttachment(String attachment) {
        taskService.deleteAttachment(attachment);
    }

    @Transactional
    public void deleteProcess(DeleteProcessRequest request) {

        for (String id : request.getIds()) {
            runtimeService.deleteProcessInstance(id, null);
        }
    }

}
