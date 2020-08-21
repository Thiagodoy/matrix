/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.workflow.service;

import com.core.matrix.dto.CommentDTO;
import com.core.matrix.dto.TaskFilterDTO;
import com.core.matrix.exceptions.ProcessIsRunningException;
import com.core.matrix.request.AddComment;
import com.core.matrix.request.CompleteTaskRequest;
import com.core.matrix.request.DeleteProcessRequest;
import com.core.matrix.request.StartProcessRequest;
import com.core.matrix.request.TaskDraftRequest;
import com.core.matrix.response.AttachmentResponse;
import com.core.matrix.response.PageResponse;
import com.core.matrix.response.ProcessDefinitionResponse;
import com.core.matrix.response.ProcessDetailResponse;
import com.core.matrix.response.ProcessInstanceStatusResponse;
import com.core.matrix.response.TaskResponse;
import com.core.matrix.utils.Constants;
import static com.core.matrix.utils.Constants.PROCESS_BILLING_CONTRACT_MESSAGE_EVENT;
import static com.core.matrix.utils.Constants.PROCESS_INFORMATION_CNPJ;
import static com.core.matrix.utils.Constants.PROCESS_INFORMATION_CONTRACT_NUMBERS;
import static com.core.matrix.utils.Constants.PROCESS_INFORMATION_MEANSUREMENT_POINT;
import static com.core.matrix.utils.Constants.PROCESS_INFORMATION_NICKNAME;
import static com.core.matrix.utils.Constants.PROCESS_INFORMATION_PROCESSO_ID;
import static com.core.matrix.utils.Constants.TASK_DRAFT;
import com.core.matrix.utils.Utils;
import com.core.matrix.workflow.model.GroupMemberActiviti;
import com.core.matrix.workflow.model.UserActiviti;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import org.activiti.engine.ActivitiTaskAlreadyClaimedException;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.HistoryService;
import org.activiti.engine.ManagementService;
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

import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Attachment;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.DelegationState;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskInfo;
import org.activiti.engine.task.TaskQuery;
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
    private ManagementService managementService;

    @Autowired
    private DataSource dataSource;

    @Transactional
    public void startProcessByMessage(String message, Map<String, Object> variables) {
        this.runtimeService.startProcessInstanceByMessage(message, variables);
    }

    @Transactional
    public String startProcessByMessage(String message) throws ProcessIsRunningException {

        this.hasInstanceRunning(message, null);
        return runtimeService.startProcessInstanceByMessage(message).getProcessInstanceId();

    }

    private void hasInstanceRunning(String message, String key) throws ProcessIsRunningException {

        if (Optional.ofNullable(key).isPresent()) {

            ProcessDefinition definition = this.repositoryService
                    .createProcessDefinitionQuery()
                    .latestVersion()
                    .processDefinitionKey(key)
                    .active()
                    .singleResult();

            long count = managementService.createJobQuery().processDefinitionId(definition.getId()).executable().count();

            if (count > 0) {
                throw new ProcessIsRunningException();
            }

        } else {
            if (message.equals(PROCESS_BILLING_CONTRACT_MESSAGE_EVENT)) {
                ProcessDefinition definition = this.repositoryService
                        .createProcessDefinitionQuery()
                        .latestVersion()
                        .processDefinitionNameLike("AGENDAMENTO DE MEDIÇÃO")
                        .active()
                        .singleResult();

                long count = managementService.createJobQuery().processDefinitionId(definition.getId()).executable().count();

                if (count > 0) {
                    throw new ProcessIsRunningException();
                }
            }
        }

    }

    @Transactional
    public Optional<TaskResponse> startProcess(StartProcessRequest request, String userId) throws Exception {

        this.hasInstanceRunning(null, request.getKey());

        if (!Optional.ofNullable(request.getVariables()).isPresent()) {
            request.setVariables(new HashMap<String, Object>());
        }

        request.getVariables().put(Constants.CREATED_BY, userId);
        request.getVariables().put(Constants.CREATED_AT, Utils.dateTimeNowFormated());
        //request.getVariables().put("taskSe", taskService);
        ProcessInstance processInstance = this.runtimeService.startProcessInstanceByKey(request.getKey(), request.getVariables());

        request.getVariables().put(PROCESS_INFORMATION_PROCESSO_ID, processInstance.getProcessInstanceId());

        this.runtimeService.setVariables(processInstance.getProcessInstanceId(), request.getVariables());

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

        verifyExistsCreatedBy(currentTask.getProcessInstanceId(), userId);

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

    private void verifyExistsCreatedBy(String processInstanceId, String userId) {

        boolean hasCreatedBy = runtimeService.hasVariable(processInstanceId, Constants.CREATED_BY);

        if (!hasCreatedBy) {
            runtimeService.setVariable(processInstanceId, Constants.CREATED_BY, userId);
        }
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

    public PageResponse<ProcessInstanceStatusResponse> findProcess(String searchValue) {

        LocalDateTime now = LocalDateTime.now();
        int daysOfMonth = Utils.getDaysOfMonth(now.toLocalDate());

        String start = LocalDateTime.of(now.getYear(), now.getMonth(), 1, 0, 1).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME).replace("T", " ");
        String end = LocalDateTime.of(now.getYear(), now.getMonth(), daysOfMonth, 23, 59).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME).replace("T", " ");

        HistoricProcessInstance process = historyService.createNativeHistoricProcessInstanceQuery()
                .sql("SELECT DISTINCT\n"
                        + "    a.PROC_INST_ID_ as ID_, a.PROC_DEF_ID_, a.START_TIME_, a.END_TIME_\n"
                        + "FROM\n"
                        + "    activiti.ACT_HI_PROCINST a\n"
                        + "        LEFT JOIN\n"
                        + "    activiti.ACT_RU_VARIABLE b ON a.PROC_INST_ID_ = b.PROC_INST_ID_\n"
                        + "        LEFT JOIN\n"
                        + "    activiti.ACT_HI_VARINST c ON a.PROC_INST_ID_ = c.PROC_INST_ID_\n"
                        + "WHERE\n"
                        + "    ((UPPER(b.TEXT_) LIKE '%" + searchValue.toUpperCase() + "%')\n"
                        + "        OR (UPPER(c.TEXT_) LIKE '%" + searchValue.toUpperCase() + "%'))\n"
                        + "        AND ((SUBSTRING(c.NAME_, 1, 1) = '#')\n"
                        + "        OR (SUBSTRING(b.NAME_, 1, 1) = '#'))\n"
                        + " AND a.START_TIME_ between '" + start + "' and '" + end + "'\n"
                        + " ORDER by a.START_TIME_ DESC")
                .listPage(0, 1)
                .get(0);

        ProcessInstanceStatusResponse status = new ProcessInstanceStatusResponse();

        status.setId(process.getId());
        String name = this.repositoryActivitiService.getProcessNameByProcessDefinitionId(process.getId());
        status.setProcessName(name);
        status.setStatus(Optional.ofNullable(process.getEndTime()).isPresent() ? "Encerrado" : "Em aberto");
        return new PageResponse<>(Arrays.asList(status), (long) 1, (long) 1, (long) 0);
    }

    public String getAllLabels(String processInstanceId) {

        List<String> informations = new ArrayList<String>();

        informations.add(MessageFormat.format("[PROCESSO]\n#{0}", processInstanceId));

        String points = this.runtimeService.getVariable(processInstanceId, PROCESS_INFORMATION_MEANSUREMENT_POINT, String.class);
        if (Optional.ofNullable(points).isPresent()) {
            String t = "[PONTOS]\n" + Arrays.asList(points.split(",")).stream().map(d -> "#" + d).collect(Collectors.joining("\n"));
            informations.add(t);
        }

        String names = this.runtimeService.getVariable(processInstanceId, PROCESS_INFORMATION_NICKNAME, String.class);
        if (Optional.ofNullable(names).isPresent()) {
            String t = "[EMPRESA]\n" + Arrays.asList(names.split(",")).stream().map(d -> "#" + d).collect(Collectors.joining("\n"));
            informations.add(t);
        }

        String cnpj = this.runtimeService.getVariable(processInstanceId, PROCESS_INFORMATION_CNPJ, String.class);
        if (Optional.ofNullable(cnpj).isPresent()) {
            String t = "[CNPJs]\n" + Arrays.asList(cnpj.split(",")).stream().map(d -> "#" + d).collect(Collectors.joining("\n"));
            informations.add(t);
        }

        String contracts = this.runtimeService.getVariable(processInstanceId, PROCESS_INFORMATION_CONTRACT_NUMBERS, String.class);
        if (Optional.ofNullable(contracts).isPresent()) {
            String t = "[CONTRATOS]\n" + Arrays.asList(contracts.split(";")).stream().map(d -> "#" + d).collect(Collectors.joining("\n"));
            informations.add(t);
        }

        return informations.stream().collect(Collectors.joining("\n"));

    }

    public List<TaskFilterDTO> getTaskFilter() {

        Connection connection = null;
        List<TaskFilterDTO> filters = new ArrayList<>();
        try {
            connection = this.dataSource.getConnection();
            Statement statement = connection.createStatement();

            LocalDateTime start = LocalDate.now().withDayOfMonth(1).atStartOfDay();
            LocalDate temp = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());
            LocalDateTime end = LocalDateTime.of(temp, LocalTime.MAX);

            String query = "SELECT DISTINCT\n"
                    + "     NAME_ as value\n"
                    + "FROM\n"
                    + "    activiti.ACT_RU_TASK t\n"
                    + "WHERE\n"
                    + "    t.CREATE_TIME_ BETWEEN {0} AND {1} \n";

            String startString = Utils.localDateTimeToMsqlString(start);
            String endString = Utils.localDateTimeToMsqlString(end);

            query = MessageFormat.format(query, startString, endString);

            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                // String type = resultSet.getString("type");
                String value = resultSet.getString("value");
                filters.add(new TaskFilterDTO("TASK_NAME", value));
            }

            query = "SELECT DISTINCT\n"
                    + "     ASSIGNEE_ as value\n"
                    + "FROM\n"
                    + "    activiti.ACT_RU_TASK t\n"
                    + "WHERE\n"
                    + "    t.CREATE_TIME_ BETWEEN {0} AND {1} \n";

            query = MessageFormat.format(query, startString, endString);

            resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                String value = resultSet.getString("value");
                filters.add(new TaskFilterDTO("USER", value));
            }
            
            
             query = "SELECT DISTINCT\n"
                    + "     PRIORITY_ as value\n"
                    + "FROM\n"
                    + "    activiti.ACT_RU_TASK t\n"
                    + "WHERE\n"
                    + "    t.CREATE_TIME_ BETWEEN {0} AND {1} \n";

            query = MessageFormat.format(query, startString, endString);

            resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                String value = resultSet.getString("value");
                filters.add(new TaskFilterDTO("PRIORITY", value));
            }

        } catch (SQLException ex) {
            Logger.getLogger(RuntimeActivitiService.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            return filters;
        }

    }

    public PageResponse<TaskResponse> getAssigneAndCandidateTask(UserActiviti user, String searchValue, String taskName, String userAssigned, Integer priority, int page, int size) {

        int min = page * size;

        TaskQuery query = taskService.createTaskQuery();

        if (Optional.ofNullable(userAssigned).isPresent()) {
            query = query.taskAssignee(userAssigned);
        } else {

            List<String> groups = user.getGroups()
                    .stream()
                    .map(g -> String.valueOf(g.getGroupId()))
                    .collect(Collectors.toList());

            //query = query.taskCandidateOrAssigned(userAssigned);
            if(!Optional.ofNullable(searchValue).isPresent()){
                query = query.taskCandidateGroupIn(groups);
            }
            
        }

        if (Optional.ofNullable(taskName).isPresent()) {
            query = query.taskName(taskName);
        }

        if(Optional.ofNullable(priority).isPresent()){                       
            query = query.taskPriority(priority);            
        }
        
        
        if (Optional.ofNullable(searchValue).isPresent()) {
            query = query.processVariableValueLike(Constants.PROCESS_LABEL, MessageFormat.format("%{0}%", searchValue.toUpperCase()));
        }

        long total = query.count();

        List<TaskResponse> response = query
                .includeTaskLocalVariables()
                .orderByTaskCreateTime()
                .desc()
                .listPage(min, size)
                .parallelStream()
                .map(t -> {
                    TaskResponse instance = new TaskResponse(t);
                    String name = this.repositoryActivitiService.getProcessNameByProcessDefinitionId(t.getProcessDefinitionId());
                    instance.setProcessDefinitionName(name);
                    return instance;
                })
                .collect(Collectors.toList());

        return new PageResponse<TaskResponse>(response, total, (long) size, (long) page);

    }

    @Transactional(readOnly = true)
    public PageResponse<TaskResponse> getMyTask(String user, String searchValue,Integer priority, int page, int size) {

        TaskQuery query = this.taskService.createTaskQuery();

        query = query.taskAssignee(user);

        if (Optional.ofNullable(searchValue).isPresent()) {
            query = query.processVariableValueLike(Constants.PROCESS_LABEL, MessageFormat.format("%{0}%", searchValue.toUpperCase()));
        }
        
        
        if(Optional.ofNullable(priority).isPresent()){
            query = query.taskPriority(priority);
        }

        int min = page * size;

        List<TaskResponse> response = query
                .includeTaskLocalVariables()
                .orderByTaskCreateTime()
                .desc()
                .listPage(min, size)
                .parallelStream()
                .map(t -> {
                    TaskResponse instance = new TaskResponse(t);
                    String name = repositoryActivitiService.getProcessNameByProcessDefinitionId(t.getProcessDefinitionId());
                    instance.setProcessDefinitionName(name);
                    return instance;
                })
                .collect(Collectors.toList());

        long sizeTotalElements = query.count();

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

        List<CommentDTO> comments = taskService
                .getProcessInstanceComments(processInstanceId)
                .stream()
                .map(c -> new CommentDTO(c))
                .sorted(Comparator.comparing(CommentDTO::getTime).reversed())
                .collect(Collectors.toList());

        comments.stream().forEach(c -> {

            if (Optional.ofNullable(c.getUserId()).isPresent()) {
                try {
                    UserActiviti user = userActivitiService.findById(c.getUserId());
                    c.setUsername(MessageFormat.format("{0} {1}", user.getFirstName(), user.getLastName()));
                    c.setPhoto(user.getPicture());
                } catch (Exception ex) {
                    Logger.getLogger(RuntimeActivitiService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        });

        detail.setComments(comments);
        List<AttachmentResponse> attachmentResponses = taskService.getProcessInstanceAttachments(processInstanceId).parallelStream().map(a -> new AttachmentResponse(a)).collect(Collectors.toList());
        detail.setAttachments(attachmentResponses);

        //HistoricProcessInstance instance = historyService.createHistoricProcessInstanceQuery().processDefinitionId(processInstanceId).singleResult();
        // detail.setProcessCreatedDate(instance.getStartTime());
        //detail.setProcessCreatedUser(instance.getStartUserId());
        return detail;

    }

    @Transactional
    public CommentDTO addComment(AddComment request, String user) {

        Comment comment = taskService.addComment(request.getTaskId(), request.getProcessInstanceId(), request.getMessage());
        commentActivitiService.setUser(comment.getId(), user);
        comment = taskService.getComment(comment.getId());
        return new CommentDTO(comment);
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

    @Transactional
    public List<TaskResponse> getTaskHistoryByProcess(String id) {

        return historyService
                .createHistoricTaskInstanceQuery()
                .processInstanceId(id)
                .orderByTaskDueDate()
                .asc()
                .list()
                .parallelStream()
                .map(ht -> {
                    return new TaskResponse(ht);
                })
                .collect(Collectors.toList());

    }

    @Transactional
    public void writeDraftOnTask(TaskDraftRequest request) {
        this.taskService.setVariableLocal(request.getTaskId(), TASK_DRAFT, request.getData());
    }

}
