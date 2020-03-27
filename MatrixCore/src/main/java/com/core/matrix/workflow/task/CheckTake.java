/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.workflow.task;

import com.core.matrix.model.Log;
import com.core.matrix.model.MeansurementFileAuthority;
import com.core.matrix.model.MeansurementFileResult;
import com.core.matrix.service.LogService;
import com.core.matrix.service.MeansurementFileAuthorityService;
import com.core.matrix.service.MeansurementFileResultService;
import static com.core.matrix.utils.Constants.*;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author Thiago
 */
public class CheckTake implements JavaDelegate {

    private static ApplicationContext context;

    private MeansurementFileResultService resultService;
    private MeansurementFileAuthorityService fileAuthorityService;
    private LogService logService;

    public CheckTake(ApplicationContext context) {
        CheckTake.context = context;
    }

    public CheckTake() {
        synchronized (CheckTake.context) {
            resultService = CheckTake.context.getBean(MeansurementFileResultService.class);
            fileAuthorityService = CheckTake.context.getBean(MeansurementFileAuthorityService.class);
            logService = CheckTake.context.getBean(LogService.class);
        }
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {

        try {

            final MeansurementFileResult result = this.getResult(execution);

            Double value = null;

            if (Optional.ofNullable(result.getAmountLiquidoAdjusted()).isPresent()) {
                Optional<MeansurementFileAuthority> opt = fileAuthorityService.findByProcess(execution.getProcessInstanceId())
                        .stream()
                        .sorted(Comparator.comparing(MeansurementFileAuthority::getId).reversed())
                        .findFirst();

                if (opt.isPresent() && opt.get().getResult().equals(CONST_APPROVED)) {
                    value = result.getAmountLiquidoAdjusted();
                } else {
                    value = result.getAmountLiquido();
                }

            } else {
                value = result.getAmountLiquido();
            }

            if (value.compareTo(result.getLimitMin()) < 0) {
                execution.setVariable(CONTROLE, RESPONSE_RECOMPRA);
            } else if (value.compareTo(result.getLimitMax()) > 0) {
                execution.setVariable(CONTROLE, RESPONSE_CURTOPRAZO);
            } else if (value.compareTo(result.getLimitMin()) >= 0 && value.compareTo(result.getLimitMax()) <= 0) {
                execution.setVariable(CONTROLE, RESPONSE_FATURAMENTO);
            }

        } catch (Exception e) {
            Logger.getLogger(CheckTake.class.getName()).log(Level.SEVERE, "[execute]", e);
            Log log = new Log();
            log.setActivitiName(execution.getCurrentActivityName());
            log.setMessageErrorApplication(e.getLocalizedMessage());
            log.setMessage("Erro ao verificar o resultado do take.");
            logService.save(log);
        }
    }

    private MeansurementFileResult getResult(DelegateExecution execution) {
        final List<MeansurementFileResult> results = resultService.getResult(execution.getProcessInstanceId());

        if (results.size() == 1) {
            return results.get(0);
        } else {
            //retorna o pai
            return results
                    .stream()
                    .filter(r -> Optional.ofNullable(r.getContractParent()).isPresent() && r.getContractParent().equals(1L))
                    .findFirst()
                    .get();
        }

    }
}