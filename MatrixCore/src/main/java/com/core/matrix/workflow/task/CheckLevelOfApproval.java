/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.workflow.task;

import com.core.matrix.model.AuthorityApproval;
import com.core.matrix.model.Log;
import com.core.matrix.model.MeansurementFileResult;
import com.core.matrix.service.AuthorityApprovalService;
import com.core.matrix.service.LogService;
import com.core.matrix.service.MeansurementFileResultService;

import static com.core.matrix.utils.Constants.CONTROLE;

import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.task.TaskInfo;
import org.springframework.context.ApplicationContext;

import static com.core.matrix.utils.Constants.*;

/**
 *
 * @author Aloysio
 */
public class CheckLevelOfApproval implements JavaDelegate {

    private static ApplicationContext context;

    private MeansurementFileResultService resultService;
    private AuthorityApprovalService approvalService;
    private LogService logService;

    List<MeansurementFileResult> resultList;

    public CheckLevelOfApproval(ApplicationContext context) {
        CheckLevelOfApproval.context = context;
    }

    public CheckLevelOfApproval() {
        synchronized (CheckLevelOfApproval.context) {
            resultService = CheckLevelOfApproval.context.getBean(MeansurementFileResultService.class);
            logService = CheckLevelOfApproval.context.getBean(LogService.class);
            approvalService = CheckLevelOfApproval.context.getBean(AuthorityApprovalService.class);
        }
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {

        try {

            TaskInfo task = execution.getEngineServices()
                    .getTaskService()
                    .createTaskQuery()
                    .executionId(execution.getId())
                    .singleResult();

            final String authority = task.getDescription();
            final AuthorityApproval approval = approvalService.findByAuthority(authority);
            final MeansurementFileResult result = this.getResult(execution);

            if (!Optional.ofNullable(result.getAmountLiquidoAdjusted()).isPresent()) {
                execution.setVariable(CONTROLE, RESPONSE_SEM_ALCADA);
            } else {

                final Double delta = Math.abs((result.getAmountLiquidoAdjusted() - result.getAmountLiquido()));

                if (delta.compareTo(approval.getMax()) > 0) {
                    if (!approval.getAuthority().equals(AUTHORITY_MAX)) {
                        execution.setVariable(CONTROLE, RESPONSE_ENCAMINHAR_APROVACAO);
                    }else{
                        execution.setVariable(CONTROLE, RESPONSE_SEM_ALCADA);
                    }
                } else {
                    execution.setVariable(CONTROLE, RESPONSE_SEM_ALCADA);
                }

            }         
        } catch (Exception e) {
            Logger.getLogger(CheckLevelOfApproval.class.getName()).log(Level.SEVERE, "[execute]", e);
            Log log = new Log();
            log.setActivitiName(execution.getCurrentActivityName());
            log.setMessageErrorApplication(e.getLocalizedMessage());
            log.setMessage("Erro ao verificar alçada de aprovação");
            logService.save(log);
        }

    }

    private MeansurementFileResult getResult(DelegateExecution execution) {
        final List<MeansurementFileResult> results = resultService.getResult(execution.getProcessInstanceId());

        if (results.size() == 1) {
            return results.get(0);
        } else {
            return results
                    .stream()
                    .filter(r -> Optional.ofNullable(r.getContractParent()).isPresent() && r.getContractParent().equals(1L))
                    .findFirst()
                    .get();
        }

    }
}
