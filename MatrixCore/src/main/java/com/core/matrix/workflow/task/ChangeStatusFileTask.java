/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.workflow.task;

import com.core.matrix.model.Log;
import com.core.matrix.service.LogService;
import com.core.matrix.service.MeansurementFileService;
import com.core.matrix.utils.MeansurementFileStatus;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author thiag
 */
public class ChangeStatusFileTask implements JavaDelegate {

    static ApplicationContext context;

    private MeansurementFileService meansurementFileService;
    private LogService logService;

    public ChangeStatusFileTask(ApplicationContext context) {
        ChangeStatusFileTask.context = context;
    }

    public ChangeStatusFileTask() {
        synchronized (ChangeStatusFileTask.context) {
            meansurementFileService = ChangeStatusFileTask.context.getBean(MeansurementFileService.class);
            logService = ChangeStatusFileTask.context.getBean(LogService.class);
        }
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {

        this.meansurementFileService
                .findByProcessInstanceId(execution.getProcessInstanceId())
                .stream()
                .forEach(file -> {

                    try {
                        meansurementFileService.updateStatus(MeansurementFileStatus.APPROVED, file.getId());
                    } catch (Exception e) {
                        Logger.getLogger(ChangeStatusFileTask.class.getName()).log(Level.SEVERE, "[execute]", e);
                        Log log = new Log();
                        log.setActivitiName(execution.getCurrentActivityName());
                        log.setMessageErrorApplication(e.getLocalizedMessage());
                        log.setMessage("Not changes status of file");
                        logService.save(log);
                    }
                });

    }

}
