/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.workflow.task;

import com.core.matrix.model.ContractCompInformation;
import com.core.matrix.model.Log;
import com.core.matrix.service.ContractCompInformationService;
import com.core.matrix.service.LogService;
import com.core.matrix.service.MeansurementFileService;
import static com.core.matrix.utils.Constants.LIST_CONTRACTS_FOR_BILLING;
import static com.core.matrix.utils.Constants.PROCESS_CONTRACTS_RELOAD_BILLING;
import static com.core.matrix.utils.Constants.PROCESS_INSTANCE_ID;
import com.core.matrix.wbc.dto.ContractDTO;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.task.Attachment;
import org.activiti.engine.task.Comment;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author thiag
 */
public class DeleteProcessInstanceTask implements JavaDelegate {

    private LogService logService;

    private ContractCompInformationService compInformationService;

    private MeansurementFileService meansurementFileService;

    private static ApplicationContext context;

    private Long contractId;

    public DeleteProcessInstanceTask() {
        synchronized (this.context) {
            this.logService = this.context.getBean(LogService.class);
            this.compInformationService = this.context.getBean(ContractCompInformationService.class);
            this.meansurementFileService = this.context.getBean(MeansurementFileService.class);
        }
    }

    public DeleteProcessInstanceTask(ApplicationContext context) {
        this.context = context;
    }

    @Transactional
    @Override
    public void execute(DelegateExecution execution) throws Exception {

        final String processInstanceId = execution.getVariable(PROCESS_INSTANCE_ID, String.class);

        try {

            final TaskService taskService = execution.getEngineServices().getTaskService();
            final RuntimeService runtimeService = execution.getEngineServices().getRuntimeService();

            runtimeService
                    .createProcessInstanceQuery()
                    .processInstanceId(processInstanceId)
                    .includeProcessVariables()
                    .list()
                    .stream()
                    .forEach(p -> {
                        final List<ContractDTO> contractDTOs = (List) p.getProcessVariables().get(LIST_CONTRACTS_FOR_BILLING);
                        setContract(contractDTOs);
                    });

            List<ContractCompInformation> list = this.compInformationService.listByContract(contractId);

            this.meansurementFileService.deleteByProcessInstance(processInstanceId);

            List<Attachment> attachments = taskService.getProcessInstanceAttachments(processInstanceId);
            List<Comment> comments = taskService.getProcessInstanceComments(processInstanceId);

            attachments.forEach(att -> {
                taskService.deleteAttachment(att.getId());
            });

            comments.forEach(com -> {
                taskService.deleteComment(com.getId());
            });

            logService.deleteLogsByProcessInstance(processInstanceId);
            runtimeService.deleteProcessInstance(processInstanceId, "Contract was updated!");

            execution.removeVariable(PROCESS_INSTANCE_ID);

            execution.setVariable(PROCESS_CONTRACTS_RELOAD_BILLING, list);

        } catch (Exception e) {
            
            Logger.getLogger(DeleteProcessInstanceTask.class.getName()).log(Level.SEVERE, "[sendEmailError]", e);
            
            execution.removeVariable(PROCESS_INSTANCE_ID);
            Log log = new Log();
            log.setActivitiName(execution.getCurrentActivityName());
            log.setProcessName(execution.getProcessBusinessKey());
            log.setProcessInstanceId(execution.getProcessInstanceId());
            log.setMessage("Erro ao deleter o processo -> " + processInstanceId);
            log.setMessageErrorApplication(e.getMessage());
            logService.save(log);
        }

    }

    private void setContract(List<ContractDTO> contractDTOs) {
        contractId = Long.valueOf(contractDTOs.stream().findFirst().get().getSNrContrato());
    }

}
