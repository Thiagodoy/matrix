/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.workflow.task;

import com.core.matrix.model.MeansurementFile;
import com.core.matrix.service.MeansurementFileDetailService;
import com.core.matrix.service.MeansurementFileService;
import com.core.matrix.utils.Constants;
import com.core.matrix.utils.MeansurementFileStatus;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author thiag
 */
public class CleanFiles implements JavaDelegate {

    
    private static ApplicationContext context;
    private MeansurementFileService fileService;
    private MeansurementFileDetailService fileDetailService;
    
    public CleanFiles(ApplicationContext context){
        CleanFiles.context = context;
    }
    
    public CleanFiles(){
        synchronized(CleanFiles.context){
            this.fileService = CleanFiles.context.getBean(MeansurementFileService.class);
            this.fileDetailService = CleanFiles.context.getBean(MeansurementFileDetailService.class);
        }
    } 
    
    
    @Override
    public void execute(DelegateExecution execution) throws Exception {

        try {
            List<String> files = (List<String>) execution.getVariable(Constants.LIST_DELETE_FILES);

            for (String file : files) {
                execution.getEngineServices().getTaskService().deleteAttachment(file);
            }
            
            this.fileService.findByProcessInstanceId(execution.getProcessInstanceId()).forEach(file->{            
                this.fileDetailService.deleteAll(file.getDetails());            
                this.fileService.updateStatus(MeansurementFileStatus.FILE_PENDING, file.getId());
            });
            
        } catch (Exception e) {
            Logger.getLogger(CleanFiles.class.getName()).log(Level.SEVERE, "[ execute ]", e);
        }

    }

}
