/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.workflow.task;

import com.core.matrix.service.MeansurementFileAuthorityService;
import com.core.matrix.service.MeansurementFileResultService;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author thiag
 */
public class ResetResultTask implements JavaDelegate {

    private static ApplicationContext context;
    private MeansurementFileAuthorityService authorityService;
    private MeansurementFileResultService meansurementFileResultService;

    public ResetResultTask(ApplicationContext context) {
        ResetResultTask.context = context;
    }

    public ResetResultTask() {

        synchronized (ResetResultTask.context) {
            this.authorityService = ResetResultTask.context.getBean(MeansurementFileAuthorityService.class);
            this.meansurementFileResultService = ResetResultTask.context.getBean(MeansurementFileResultService.class);
        }
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {

        try {

            this.authorityService.deleteByProcessInstanceId(execution.getProcessInstanceId());
            this.meansurementFileResultService
                    .findByIdProcess(execution.getProcessInstanceId())
                    .stream()
                    .forEach(file -> {
                        try {
                            file.setAmountLiquidoAdjusted(null);
                            this.meansurementFileResultService.update(file);
                        } catch (Exception ex) {
                            Logger.getLogger(ResetResultTask.class.getName()).log(Level.SEVERE, "Can't update entity", ex);
                        }
                    });

        } catch (Exception e) {
            Logger.getLogger(ResetResultTask.class.getName()).log(Level.SEVERE, "[ execute ]", e);
        }
    }

}
