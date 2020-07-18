/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.workflow.task.listener;

import static com.core.matrix.utils.Constants.VAR_LIST_FILES;
import static com.core.matrix.utils.Constants.VAR_MAP_DETAILS;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.ExecutionListener;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.delegate.VariableScope;

/**
 *
 * @author thiag
 */
public class RemoveVariablesFromProcessListener implements TaskListener, ExecutionListener {

    @Override
    public void notify(DelegateTask delegateTask) {
       this.execute(delegateTask);
    }

    @Override
    public void notify(DelegateExecution execution) throws Exception {
       this.execute(execution);
    }
    
    
    private void execute(VariableScope x){
         if (x.hasVariable(VAR_MAP_DETAILS) || x.hasVariable(VAR_LIST_FILES)) {
            x.removeVariable(VAR_MAP_DETAILS);
            x.removeVariable(VAR_LIST_FILES);
        }
    }

}
