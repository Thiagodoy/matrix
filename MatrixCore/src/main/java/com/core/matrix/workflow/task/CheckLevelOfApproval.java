/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.workflow.task;

import com.core.matrix.model.AuthorityApproval;
import com.core.matrix.model.Log;
import com.core.matrix.model.MeansurementFileAuthority;
import com.core.matrix.model.MeansurementFileResult;
import com.core.matrix.service.AuthorityApprovalService;
import com.core.matrix.service.LogService;
import com.core.matrix.service.MeansurementFileAuthorityService;
import com.core.matrix.service.MeansurementFileResultService;

import static com.core.matrix.utils.Constants.CONTROLE;

import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.context.ApplicationContext;
import static com.core.matrix.utils.Constants.*;
import java.util.Comparator;
import org.activiti.engine.impl.el.FixedValue;

/**
 *
 * @author Thiago
 */
public class CheckLevelOfApproval implements JavaDelegate {

    private static ApplicationContext context;

    private MeansurementFileResultService resultService;
    private MeansurementFileAuthorityService fileAuthorityService;

    private AuthorityApprovalService approvalService;
    private LogService logService;
    public FixedValue profile;

    public CheckLevelOfApproval(ApplicationContext context) {
        CheckLevelOfApproval.context = context;
    }

    public CheckLevelOfApproval() {
        synchronized (CheckLevelOfApproval.context) {
            resultService = CheckLevelOfApproval.context.getBean(MeansurementFileResultService.class);
            logService = CheckLevelOfApproval.context.getBean(LogService.class);
            approvalService = CheckLevelOfApproval.context.getBean(AuthorityApprovalService.class);
            fileAuthorityService = CheckLevelOfApproval.context.getBean(MeansurementFileAuthorityService.class);
        }
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {

        try {

            final MeansurementFileResult result = this.getResult(execution);

            if (!Optional.ofNullable(result.getAmountLiquidoAdjusted()).isPresent()) {
                execution.setVariable(CONTROLE, RESPONSE_SEM_ALCADA);
            } else {

                final String authority = profile.getExpressionText();

                final Double delta = Math.abs((result.getAmountLiquidoAdjusted() - result.getAmountLiquido()));
                final AuthorityApproval approval = approvalService.findBetween(delta);

                

                if (delta.compareTo(approval.getMax()) <= 0) {
                    execution.setVariable(CONTROLE, RESPONSE_SEM_ALCADA);
                } else if (delta.compareTo(approval.getMax()) > 0) {
                    if (authority.equals(approval.getAuthority())) {
                        execution.setVariable(CONTROLE, RESPONSE_SEM_ALCADA);
                    } else {
                        execution.setVariable(CONTROLE, RESPONSE_ENCAMINHAR_APROVACAO);
                    }
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
            //busca o contrato pai
            return results
                    .stream()
                    .filter(r -> Optional.ofNullable(r.getContractParent()).isPresent() && r.getContractParent().equals(1L))
                    .findFirst()
                    .get();
        }

    }
}
