/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.workflow.task;

import org.activiti.engine.delegate.DelegateExecution;
import org.joda.time.LocalDate;
import static com.core.matrix.utils.Constants.PROCESS_MONTH_REFERENCE;
import static com.core.matrix.utils.Constants.PROCESS_YEAR_REFERENCE;
/**
 *
 * @author Aloysio
 */
public class SetMonthYear extends Task {
    
    
    @Override
    public void execute(DelegateExecution execution) throws Exception {
        
        LocalDate monthBilling = LocalDate.now().minusMonths(1);

        Long month = Integer.valueOf(monthBilling.getMonthOfYear()).longValue();
        Long year = Integer.valueOf(monthBilling.getYear()).longValue();
        
        execution.setVariable(PROCESS_MONTH_REFERENCE, month);
        execution.setVariable(PROCESS_YEAR_REFERENCE, year);

    }
    
}
