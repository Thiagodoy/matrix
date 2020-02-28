/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.workflow.task;

import com.core.matrix.service.MeansurementFileService;
import static com.core.matrix.utils.Constants.CONTROLE;
import static com.core.matrix.utils.Constants.PROCESS_MONTH_REFERENCE;
import static com.core.matrix.utils.Constants.PROCESS_YEAR_REFERENCE;
import static com.core.matrix.utils.Constants.RESPONSE_STATUS_FILE_NOK;
import static com.core.matrix.utils.Constants.RESPONSE_STATUS_FILE_OK;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author thiag
 */
public class CheckStatusFileResultTask implements JavaDelegate {

    private static ApplicationContext context;
    private MeansurementFileService fileService;

    public CheckStatusFileResultTask(ApplicationContext context) {
        CheckStatusFileResultTask.context = context;
    }

    public CheckStatusFileResultTask() {
        synchronized (CheckStatusFileResultTask.context) {
            fileService = CheckStatusFileResultTask.context.getBean(MeansurementFileService.class);

        }
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {

        try {
            final Long monthReference = execution.getVariable(PROCESS_MONTH_REFERENCE, Long.class);
            final Long yearReference = execution.getVariable(PROCESS_YEAR_REFERENCE, Long.class);

            if (fileService.hasFilePending(yearReference, monthReference)) {
                execution.setVariable(CONTROLE, RESPONSE_STATUS_FILE_NOK);
            } else {
                execution.setVariable(CONTROLE, RESPONSE_STATUS_FILE_OK);
            }
        } catch (Exception e) {
            Logger.getLogger(CheckStatusFileResultTask.class.getName()).log(Level.SEVERE, "[execute]", e);
        }

    }

}
