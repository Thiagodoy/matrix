/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.workflow.task;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author thiag
 */
public class CalculateTask implements  JavaDelegate{

    
    private static ApplicationContext context;
    
    
    public CalculateTask(){
        
    }
    
    public CalculateTask(ApplicationContext context){
        CalculateTask.context = context;
    }
    
    
    @Override
    public void execute(DelegateExecution de) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
