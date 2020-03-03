/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.workflow.task;

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
public class CleanFileResult implements JavaDelegate {

    private static ApplicationContext context;    
    private MeansurementFileResultService  fileResultService;
    

    public CleanFileResult(ApplicationContext context) {
        CleanFileResult.context = context;
    }

    public CleanFileResult() {
        synchronized (CleanFileResult.context) {            
            this.fileResultService = CleanFileResult.context.getBean(MeansurementFileResultService.class);
        }
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {

        try {           
            fileResultService.deleteByProcess(execution.getProcessInstanceId());
        } catch (Exception e) {
            Logger.getLogger(CleanFileResult.class.getName()).log(Level.SEVERE, "[ execute ]", e);
        }

    }

}
