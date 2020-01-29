/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.workflow.task.listener;

import static com.core.matrix.utils.Constants.*;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

/**
 *
 * @author thiag
 */
public class AssigneUserListener implements TaskListener{

    @Override
    public void notify(DelegateTask delegateTask) {       
        
        delegateTask.getCandidates();        
        String user = delegateTask.getVariable(CREATED_BY,String.class);
        delegateTask.setAssignee(user);
    }
    
}
