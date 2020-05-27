/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.workflow.listener;

import com.core.matrix.model.Email;
import com.core.matrix.model.Notification;
import com.core.matrix.model.Template;
import com.core.matrix.service.NotificationService;
import com.core.matrix.service.TemplateService;
import com.core.matrix.specifications.TemplateSpecification;
import com.core.matrix.utils.Constants;
import com.core.matrix.utils.ThreadPoolEmail;
import com.core.matrix.utils.Utils;
import com.core.matrix.workflow.model.GroupActiviti;
import com.core.matrix.workflow.model.UserActiviti;
import com.core.matrix.workflow.service.GroupActivitiService;
import com.core.matrix.workflow.service.RepositoryActivitiService;
import com.core.matrix.workflow.service.UserActivitiService;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.activiti.engine.IdentityService;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiEventListener;
import org.activiti.engine.task.Task;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

/**
 *
 * @author thiag
 */
public class RuntimeListener implements ActivitiEventListener {

    private final TemplateService templateService;
    private final UserActivitiService userActivitiService;
    private final ThreadPoolEmail threadPoolEmail;
    private final IdentityService identityService;
    private final RepositoryActivitiService repositoryActivitiService;
    private final GroupActivitiService groupActivitiService;
    private final NotificationService notificationService;

    public RuntimeListener(ApplicationContext context, IdentityService identityService) {

        synchronized (context) {
            this.templateService = context.getBean(TemplateService.class);
            this.userActivitiService = context.getBean(UserActivitiService.class);
            this.threadPoolEmail = context.getBean(ThreadPoolEmail.class);
            this.identityService = identityService;
            this.repositoryActivitiService = context.getBean(RepositoryActivitiService.class);
            this.groupActivitiService = context.getBean(GroupActivitiService.class);
            this.notificationService = context.getBean(NotificationService.class);
        }

    }

    @Override
    public void onEvent(ActivitiEvent event) {

        final String executionId = event.getExecutionId();

        List<Email> emails = new ArrayList<>();
        List<Notification> notifications = new ArrayList<>();
        List<UserActiviti> users = new ArrayList<>();
        Task task = null;
        switch (event.getType()) {
            case TASK_ASSIGNED:

                task = event.getEngineServices()
                        .getTaskService()
                        .createTaskQuery()
                        .executionId(executionId)
                        .singleResult();

                String userEmail = task.getAssignee();
                users = Arrays.asList(this.getUserByEmail(userEmail));
                emails = this.prepareEmails(users, Template.TemplateBusiness.USER_TASK_PENDING, task);
                notifications = this.prepareNotifications(users, task, com.core.matrix.model.Notification.NotificationType.USER_TASK_PENDING);
                this.send(emails, notifications);
                break;

            case TASK_CREATED:

                final TaskService taskService = event.getEngineServices().getTaskService();

                new Thread(() -> {

                    try {
                        Thread.sleep(5000);

                        Task task1 = taskService
                                .createTaskQuery()
                                .executionId(executionId)
                                .singleResult();

                        if (Optional.ofNullable(task1.getAssignee()).isPresent()) {
                            return;
                        }

                        final String processInstanceID = event.getProcessDefinitionId();
                        List<UserActiviti> users1 = this.getUsersFromTaskAndProcessDef(task1.getId(), processInstanceID);
                        List<Email> emails1 = this.prepareEmails(users1, Template.TemplateBusiness.GROUP_TASK_PENDING, task1);
                        List<Notification> notifications1 = this.prepareNotifications(users1, task1, com.core.matrix.model.Notification.NotificationType.GROUP_TASK_PENDING);

                        this.send(emails1, notifications1);

                    } catch (InterruptedException ex) {
                        Logger.getLogger(RuntimeListener.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }).start();

                break;

            case TASK_COMPLETED:

                task = event.getEngineServices()
                        .getTaskService()
                        .createTaskQuery()
                        .executionId(executionId)
                        .singleResult();

                this.notificationService.pushActionRemoveTask(task.getId());

                break;

        }

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
            Specification spc = TemplateSpecification.filter(null, null, null, type);
            Template template = (Template) templateService.find(spc, Pageable.unpaged()).getContent().get(0);

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm");

            String taskDateCreation = dateFormat.format(task.getCreateTime());

            users.forEach(u -> {

                if (u.isReceiveEmail()) {

                    Map<String, String> data = new HashMap<String, String>();
                    data.put(Constants.TEMPLATE_PARAM_USER_EMAIL, u.getEmail());
                    data.put(Constants.TEMPLATE_PARAM_USER_NAME, u.getFirstName());
                    data.put(Constants.TEMPLATE_PARAM_TASK_NAME, task.getName());
                    data.put(Constants.TEMPLATE_PARAM_PROCESS_NAME, this.getProcessDefinitionName(task.getProcessDefinitionId()));
                    data.put(Constants.TEMPLATE_PARAM_NUMBER_PROCESS, task.getProcessInstanceId());
                    data.put(Constants.TEMPLATE_PARAM_TASK_CREATE_DATE, taskDateCreation);
                    data.put(Constants.TEMPLATE_PARAM_GROUP_NAME, this.getGroupsName(u));

                    String emailData = Utils.mapToString(data);

                    Email email = new Email();
                    email.setTemplate(template);
                    email.setData(emailData);

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

        return names.stream().collect(Collectors.joining("#"));

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

    private synchronized List<UserActiviti> getUsersFromTaskAndProcessDef(String task, String processDefinitionId) {

        List<UserActiviti> users = new ArrayList<>();

        identityService.createNativeUserQuery().sql("SELECT \n"
                + "    u.*\n"
                + "FROM\n"
                + "    activiti.act_ru_identitylink ari\n"
                + "        LEFT JOIN\n"
                + "    activiti.act_id_membership aim ON ari.GROUP_ID_ = aim.GROUP_ID_\n"
                + "        LEFT JOIN\n"
                + "    activiti.act_id_user u ON aim.USER_ID_ = u.ID_\n"
                + "WHERE\n"
                + "    ari.TYPE_ = 'candidate'\n"
                + "        AND (ari.TASK_ID_ = '" + task + "'\n"
                + "        OR ari.PROC_DEF_ID_ = '" + processDefinitionId + "')").list().stream().forEach(u -> {
                    UserActiviti user = this.getUserByEmail(u.getId());
                    if (Optional.ofNullable(user).isPresent()) {
                        users.add(user);
                    }
                });

        return users;

    }

    private UserActiviti getUserByEmail(String email) {
        try {
            return userActivitiService.findById(email);
        } catch (Exception e) {
            Logger.getLogger(RuntimeListener.class.getName()).log(Level.SEVERE, "[getUserByEmail]", e);
            return null;
        }
    }

    @Override
    public boolean isFailOnException() {
        return true;
    }

}
