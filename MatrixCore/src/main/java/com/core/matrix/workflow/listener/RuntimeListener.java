/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.workflow.listener;

import com.core.matrix.factory.EmailFactory;
import com.core.matrix.model.Email;
import com.core.matrix.model.Notification;
import com.core.matrix.model.Parameters;
import com.core.matrix.model.Template;
import com.core.matrix.service.NotificationService;
import com.core.matrix.service.ParametersService;
import com.core.matrix.utils.Constants;
import static com.core.matrix.utils.Constants.PROCESS_ASSOCIATE_USER_AFTER_SALES;
import com.core.matrix.utils.ThreadPoolEmail;
import com.core.matrix.workflow.model.GroupActiviti;
import com.core.matrix.workflow.model.UserActiviti;
import com.core.matrix.workflow.service.GroupActivitiService;
import com.core.matrix.workflow.service.RepositoryActivitiService;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiEventListener;
import org.activiti.engine.task.Task;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author thiag
 */
public class RuntimeListener implements ActivitiEventListener {

    private final ThreadPoolEmail threadPoolEmail;

    private final RepositoryActivitiService repositoryActivitiService;
    private final GroupActivitiService groupActivitiService;
    private final NotificationService notificationService;
    private final EmailFactory emailFactory;
    private final ParametersService parametersService;
    private TaskService taskService;
    private RuntimeService runtimeService;
    private final static ThreadPoolExecutor pool = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);

    public RuntimeListener(ApplicationContext context, IdentityService identityService) {

        synchronized (context) {
            this.threadPoolEmail = context.getBean(ThreadPoolEmail.class);
            this.repositoryActivitiService = context.getBean(RepositoryActivitiService.class);
            this.groupActivitiService = context.getBean(GroupActivitiService.class);
            this.notificationService = context.getBean(NotificationService.class);
            this.emailFactory = context.getBean(EmailFactory.class);
            this.parametersService = context.getBean(ParametersService.class);
        }

    }

    @Override
    public void onEvent(ActivitiEvent event) {

        if (!Optional.ofNullable(this.taskService).isPresent()) {
            this.taskService = event.getEngineServices().getTaskService();
            this.runtimeService = event.getEngineServices().getRuntimeService();
        }

        Task task = null;
        switch (event.getType()) {

            case TASK_CREATED:
                assineeTask(event);
                prepareEmails(event, Template.TemplateBusiness.GROUP_TASK_PENDING, Notification.NotificationType.GROUP_TASK_PENDING);                
                break;

            case TASK_COMPLETED:

                task = this.getTask(event);

                if (Optional.ofNullable(task).isPresent()) {
                    synchronized (this.notificationService) {
                        this.notificationService.deleteNotificationByTaskId(task.getId());
                        this.notificationService.pushActionRemoveByTaskId(task.getId());
                    }
                }

                break;

        }

    }

    private Task getTask(ActivitiEvent event) {

        try {
            return event.getEngineServices()
                    .getTaskService()
                    .createTaskQuery()
                    .executionId(event.getExecutionId())
                    .singleResult();
        } catch (Exception e) {
            return null;
        }
    }

    private void prepareEmails(ActivitiEvent event, Template.TemplateBusiness template, com.core.matrix.model.Notification.NotificationType notificationType) {

        pool.submit(() -> {

            try {
                final ActivitiEvent events = event;

                Thread.sleep(1000);

                Task task;
                try {
                    task = this.taskService.createTaskQuery().executionId(events.getExecutionId()).singleResult();
                } catch (Exception e) {
                    Logger.getLogger(RuntimeListener.class.getName()).log(Level.SEVERE, "Não localizou a tarefa para o envio de email exeid-> " + events.getExecutionId(), e);
                    task = null;
                }

                if (!Optional.ofNullable(task).isPresent()) {
                    return;
                }

                Optional<Parameters> parameterOpt1 = parametersService.findByKey(task.getTaskDefinitionKey());
                if (parameterOpt1.isPresent()) {
                    Parameters parameter = parameterOpt1.get();
                    if (parameter.getType().equals(Parameters.ParameterType.BOOLEAN) && (Boolean) parameter.getValue()) {
                        Set<UserActiviti> usersEmails = new HashSet<>();
                        this.groupActivitiService.getGroupByTaskOrProcessDefId(task.getId(), events.getProcessDefinitionId()).forEach(group -> {
                            usersEmails.addAll(this.groupActivitiService.getUsersByIdGroup(group));
                        });

                        List<UserActiviti> users = usersEmails.stream().collect(Collectors.toList());
                        List<Email> emails1 = this.prepareEmails(users, template, task);

                        List<Notification> notifications1 = this.prepareNotifications(users, task, notificationType);
                        this.send(emails1, notifications1);
                    }
                }
            } catch (Exception e) {
                Logger.getLogger(RuntimeListener.class.getName()).log(Level.SEVERE, "[Erro ao enviar o email]", e);
            }
        });
    }

    private void assineeTask(ActivitiEvent e) {

        pool.submit(() -> {

            try {

                final ActivitiEvent event = e;

                Thread.sleep(1000);

                Task task;
                task = this.taskService.createTaskQuery()
                        .processInstanceId(event.getProcessInstanceId())
                        .includeProcessVariables()
                        .singleResult();

                if (Optional.ofNullable(task).isPresent()) {
                    if (task.getProcessVariables().containsKey(PROCESS_ASSOCIATE_USER_AFTER_SALES)) {
                        String userId = (String) task.getProcessVariables().get(PROCESS_ASSOCIATE_USER_AFTER_SALES);
                        //event.getEngineServices().getTaskService().claim(task.getId(), userId);
                        this.taskService.setAssignee(task.getId(), userId);
                        this.runtimeService.removeVariable(task.getProcessInstanceId(), PROCESS_ASSOCIATE_USER_AFTER_SALES);
                    }
                }

            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "[assineeTask]", ex);
            }

        });
    }

    private synchronized void send(List<Email> emails, List<Notification> notifications) {
        notificationService.push(notifications);
        threadPoolEmail.submit(emails);
    }

    private synchronized List<Notification> prepareNotifications(List<UserActiviti> users, Task task, Notification.NotificationType type) {

        List<Notification> notifications = new ArrayList<>();

        users.stream().forEach(u -> {

            if (u.isReceiveNotification()) {
                Notification notification = new Notification();
                notification.setNameProcessId(this.getProcessDefinitionName(task.getProcessDefinitionId()));
                notification.setNameTask(task.getName());
                notification.setProcessId(task.getProcessInstanceId());
                notification.setTaskId(task.getId());
                notification.setTo(u.getId());
                notification.setType(type);
                notifications.add(notification);
                notification.setForm(task.getFormKey());
            }
        });

        return notifications;
    }

    private synchronized List<Email> prepareEmails(List<UserActiviti> users, Template.TemplateBusiness type, Task task) {

        List<Email> emails = new ArrayList<>();

        try {

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm");

            String taskDateCreation = dateFormat.format(task.getCreateTime());

            users.forEach(u -> {

                if (u.isReceiveEmail()) {

                    Email email = emailFactory.createEmailTemplate(type);

                    email.setParameter(Constants.TEMPLATE_PARAM_USER_EMAIL, u.getEmail());
                    email.setParameter(Constants.TEMPLATE_PARAM_USER_NAME, u.getFirstName());
                    email.setParameter(Constants.TEMPLATE_PARAM_TASK_NAME, task.getName());
                    email.setParameter(Constants.TEMPLATE_PARAM_PROCESS_NAME, this.getProcessDefinitionName(task.getProcessDefinitionId()));
                    email.setParameter(Constants.TEMPLATE_PARAM_NUMBER_PROCESS, task.getProcessInstanceId());
                    email.setParameter(Constants.TEMPLATE_PARAM_TASK_CREATE_DATE, taskDateCreation);
                    email.setParameter(Constants.TEMPLATE_PARAM_GROUP_NAME, this.getGroupsName(u));

                    emails.add(email);
                }

            });

        } catch (Exception e) {
            Logger.getLogger(RuntimeListener.class.getName()).log(Level.SEVERE, "[prepareEmails]", e);
        }

        return emails;

    }

    private String getGroupsName(UserActiviti user) {

        List<String> names = new ArrayList<>();

        user.getGroups().forEach(g -> {

            try {

                GroupActiviti groupActiviti = groupActivitiService.findById(g.getGroupId());
                names.add(groupActiviti.getName());

            } catch (Exception e) {
                names.add("");
            }
        });

        return names.stream().collect(Collectors.toList()).get(0);

    }

    private String getProcessDefinitionName(String processDefinitionId) {

        return repositoryActivitiService
                .listAll()
                .stream()
                .filter(p -> p.getId().equals(processDefinitionId))
                .map(t -> t.getName())
                .findFirst()
                .orElse("");

    }

    @Override
    public boolean isFailOnException() {
        return true;
    }

}
