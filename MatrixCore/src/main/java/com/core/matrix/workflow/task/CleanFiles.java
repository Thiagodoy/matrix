/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.workflow.task;

import com.core.matrix.utils.Constants;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

/**
 *
 * @author thiag
 */
public class CleanFiles implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) throws Exception {

        try {
            List<String> files = (List<String>) execution.getVariable(Constants.LIST_DELETE_FILES);

            for (String file : files) {
                execution.getEngineServices().getTaskService().deleteAttachment(file);
            }
        } catch (Exception e) {
            Logger.getLogger(CleanFiles.class.getName()).log(Level.SEVERE, "[ execute ]", e);
        }

    }

}
