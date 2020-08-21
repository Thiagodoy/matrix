/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.workflow.task.listener;

import com.core.matrix.model.ContractMtx;
import static com.core.matrix.utils.Constants.*;
import java.util.List;
import java.util.Optional;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

/**
 *
 * @author thiag
 */
public class TaskLabelListener implements TaskListener {

    @Override
    public void notify(DelegateTask delegateTask) {

        if (delegateTask.hasVariable(PROCESS_INFORMATION_CONTRACTS_MATRIX)) {

            List<ContractMtx> contractsMtx = delegateTask.getVariable(PROCESS_INFORMATION_CONTRACTS_MATRIX, List.class);

            if (contractsMtx.size() == 1) {
                ContractMtx contractMtx = contractsMtx.get(0);
                delegateTask.setVariableLocal(TASK_LABEL, contractMtx.getNickname());
                delegateTask.setVariableLocal(TASK_LABEL_CONTRACT, contractMtx.getWbcContract());                 
            } else {
                Optional<ContractMtx> opt = contractsMtx.stream().filter(c -> c.isFather()).findFirst();
                if (opt.isPresent()) {
                    ContractMtx contractMtx = opt.get();
                    delegateTask.setVariableLocal(TASK_LABEL, contractMtx.getNickname());
                    delegateTask.setVariableLocal(TASK_LABEL_CONTRACT, contractMtx.getWbcContract());
                }
              
            }
        }
        
        if(delegateTask.hasVariable(PROCESS_GLOBAL_PRIORITY)){
            Long priority = delegateTask.getVariable(PROCESS_GLOBAL_PRIORITY, Long.class);
            delegateTask.setPriority(priority.intValue());
        }else{
            delegateTask.setPriority(4);
        }
        
    }
}
