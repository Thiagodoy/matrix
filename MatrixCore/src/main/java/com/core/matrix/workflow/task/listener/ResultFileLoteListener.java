/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.workflow.task.listener;

import com.core.matrix.factory.EmailFactory;
import com.core.matrix.model.Email;
import com.core.matrix.model.Notification;
import com.core.matrix.model.Template;
import com.core.matrix.service.NotificationService;
import static com.core.matrix.utils.Constants.*;
import com.core.matrix.utils.ThreadPoolEmail;
import com.core.matrix.workflow.service.RepositoryActivitiService;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author thiag
 */
public class ResultFileLoteListener implements TaskListener {

    private EmailFactory emailFactory;
    private ThreadPoolEmail threadPoolEmail;
    private NotificationService notificationService;
    private RepositoryActivitiService repositoryActivitiService;
    private static ApplicationContext context;

    public ResultFileLoteListener(ApplicationContext context) {
        ResultFileLoteListener.context = context;
    }

    public ResultFileLoteListener() {
        synchronized (this.context) {
            emailFactory = context.getBean(EmailFactory.class);
            threadPoolEmail = context.getBean(ThreadPoolEmail.class);
            notificationService = context.getBean(NotificationService.class);
            repositoryActivitiService = context.getBean(RepositoryActivitiService.class);
        }
    }

    @Override
    public void notify(DelegateTask delegateTask) {

        try {
            this.sendEmail(delegateTask);
            this.sendNotification(delegateTask);
        } catch (Exception e) {
            Logger.getLogger(ResultFileLoteListener.class.getName()).log(Level.SEVERE, "[notify]", e);
        }

    }

    private void sendEmail(DelegateTask delegateTask) {

        String to = delegateTask.getVariable(CREATED_BY, String.class);

        String nameUser = delegateTask
                .getExecution()
                .getEngineServices()
                .getIdentityService()
                .createUserQuery()
                .userId(to)
                .singleResult()
                .getFirstName();

        Email email = emailFactory.createEmailTemplate(Template.TemplateBusiness.FINISHED_UPLOAD_LOTE_FILE);

        email.setParameter(TEMPLATE_PARAM_USER_NAME, nameUser);
        email.setParameter(TEMPLATE_PARAM_NUMBER_PROCESS, delegateTask.getProcessInstanceId());
        email.setParameter(TEMPLATE_PARAM_USER_EMAIL, to);

        threadPoolEmail.submit(email);
    }

    private void sendNotification(DelegateTask delegateTask) {

        String to = delegateTask.getVariable(CREATED_BY, String.class);
        Notification notification = new Notification();
        notification.setNameProcessId(this.getProcessDefinitionName(delegateTask.getProcessDefinitionId()));
        notification.setNameTask(delegateTask.getName());
        notification.setProcessId(delegateTask.getProcessInstanceId());
        notification.setTaskId(delegateTask.getId());
        notification.setTo(to);
        notification.setType(Notification.NotificationType.GROUP_TASK_PENDING);
        notification.setForm(delegateTask.getFormKey());        
        notificationService.push(notification);

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

}
