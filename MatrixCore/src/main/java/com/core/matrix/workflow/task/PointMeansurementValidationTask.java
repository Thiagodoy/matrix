/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.workflow.task;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

/**
 *
 * @author thiag
 */
@Component
public class PointMeansurementValidationTask implements JavaDelegate {

    private final static String FILE_MEANSUREMENT_ID = "";
    
    

    @Override
    public void execute(DelegateExecution de) throws Exception {

        try {            
            Long id = Long.getLong(String.valueOf(de.getVariable(FILE_MEANSUREMENT_ID)));           
            
        } catch (Exception e) {
            
            throw e;
        }
 
        
        
        

    }

}
