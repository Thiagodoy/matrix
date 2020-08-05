/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.workflow.task;

import com.core.matrix.model.ContractMtx;
import com.core.matrix.model.Log;
import com.core.matrix.model.MeansurementFile;
import com.core.matrix.service.ContractMtxService;
import com.core.matrix.service.LogService;
import com.core.matrix.service.MeansurementFileAuthorityService;
import com.core.matrix.service.MeansurementFileDetailService;
import com.core.matrix.service.MeansurementFileService;
import com.core.matrix.service.MeansurementRepurchaseService;
import static com.core.matrix.utils.Constants.LIST_CONTRACTS_FOR_BILLING;
import static com.core.matrix.utils.Constants.PROCESS_CONTRACTS_RELOAD_BILLING;
import static com.core.matrix.utils.Constants.PROCESS_INSTANCE_ID;
import com.core.matrix.wbc.dto.ContractDTO;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.runtime.ProcessInstance;
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

    private ContractMtxService contractMtxService;

    private MeansurementFileService meansurementFileService;

    private MeansurementFileDetailService meansurementFileDetailService;

    private MeansurementRepurchaseService meansurementRepurchaseService;

    private MeansurementFileAuthorityService meansurementFileAuthorityService;

    private static ApplicationContext context;
    
    private DataSource dataSource;
    

    private Long contractId;

    public DeleteProcessInstanceTask() {
        synchronized (DeleteProcessInstanceTask.context) {
            this.logService = DeleteProcessInstanceTask.context.getBean(LogService.class);
            this.contractMtxService = DeleteProcessInstanceTask.context.getBean(ContractMtxService.class);
            this.meansurementFileService = DeleteProcessInstanceTask.context.getBean(MeansurementFileService.class);
            this.meansurementRepurchaseService = DeleteProcessInstanceTask.context.getBean(MeansurementRepurchaseService.class);
            this.meansurementFileAuthorityService = DeleteProcessInstanceTask.context.getBean(MeansurementFileAuthorityService.class);
            this.meansurementFileDetailService = DeleteProcessInstanceTask.context.getBean(MeansurementFileDetailService.class);
            
        }
    }

    public DeleteProcessInstanceTask(ApplicationContext context) {
        DeleteProcessInstanceTask.context = context;
    }

    
    //@Transactional(transactionManager = "matrixTransactionManager")
    @Override
    public void execute(DelegateExecution execution) throws Exception {

        final String processInstanceId = execution.getVariable(PROCESS_INSTANCE_ID, String.class);

        try {

            final TaskService taskService = execution.getEngineServices().getTaskService();
            final RuntimeService runtimeService = execution.getEngineServices().getRuntimeService();
            final HistoryService historyService = execution.getEngineServices().getHistoryService();

            Optional<ProcessInstance> opt = Optional.ofNullable(runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult());

            if (opt.isPresent()) {
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
            } else {
                historyService
                        .createHistoricProcessInstanceQuery()
                        .processInstanceId(processInstanceId)
                        .includeProcessVariables()
                        .list()
                        .stream()
                        .forEach(p -> {
                            final List<ContractDTO> contractDTOs = (List) p.getProcessVariables().get(LIST_CONTRACTS_FOR_BILLING);
                            setContract(contractDTOs);
                        });
            }

            List<ContractMtx> list = this.contractMtxService.findAll(contractId).getContracts();

            this.meansurementRepurchaseService.deleteByProcessInstanceId(processInstanceId);

            List<Long> ids = this.meansurementFileService.findByProcessInstanceId(processInstanceId)
                    .parallelStream()
                    .mapToLong(MeansurementFile::getId)
                    .boxed()
                    .collect(Collectors.toList());

            this.meansurementFileDetailService.deleteByMeansurementFileId(ids);
            this.meansurementFileService.deleteByProcessInstance(processInstanceId);
            this.meansurementFileAuthorityService.deleteByProcessInstanceId(processInstanceId);

            List<Attachment> attachments = taskService.getProcessInstanceAttachments(processInstanceId);
            List<Comment> comments = taskService.getProcessInstanceComments(processInstanceId);

            attachments.forEach(att -> {
                taskService.deleteAttachment(att.getId());
            });

            comments.forEach(com -> {
                taskService.deleteComment(com.getId());
            });

            logService.deleteLogsByProcessInstance(processInstanceId);

            if (opt.isPresent()) {
                runtimeService.deleteProcessInstance(processInstanceId, "Contract information was updated!");
            }

            execution.removeVariable(PROCESS_INSTANCE_ID);

            execution.setVariable(PROCESS_CONTRACTS_RELOAD_BILLING, list);

        } catch (Exception e) {
            Logger.getLogger(DeleteProcessInstanceTask.class.getName()).log(Level.SEVERE, "[execute]", e);
            execution.removeVariable(PROCESS_INSTANCE_ID);
            Log log = new Log();
            log.setActivitiName(execution.getCurrentActivityName());
            log.setProcessInstanceId(execution.getProcessInstanceId());
            log.setMessage("Erro ao deleter o processo -> " + processInstanceId);
            log.setMessageErrorApplication(e.getMessage());
            logService.save(log);
        }

    }

    private void setContract(List<ContractDTO> contractDTOs) {

        if (!Optional.ofNullable(this.contractId).isPresent()) {
            contractId = Long.valueOf(contractDTOs.stream().findFirst().get().getSNrContrato());
        }
    }

}
