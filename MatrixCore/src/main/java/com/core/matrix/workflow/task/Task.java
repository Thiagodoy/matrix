/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.workflow.task;

import com.core.matrix.utils.Constants;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.stereotype.Component;

/**
 *
 * @author thiag
 */
@Component
public class Task implements JavaDelegate{

    
    
    private DelegateExecution delegateExecution;
    
    @Override
    public void execute(DelegateExecution de) throws Exception {
        this.delegateExecution = de; 
    }
    
    
    public void completeTaskSuccess(String value){        
        this.delegateExecution.setVariable(Constants.CONTROLE, value);        
    } 
    
    public void completeTaskError(String value){        
        this.delegateExecution.setVariable(Constants.CONTROLE, value);        
    } 
}
