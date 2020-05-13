/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.jobs;

import com.core.matrix.model.Log;
import com.core.matrix.service.LogService;
import static com.core.matrix.utils.Constants.PROCESS_MONTH_REFERENCE;
import static com.core.matrix.utils.Constants.PROCESS_STATUS_PROCESS_FILE_MESSAGE_EVENT;
import static com.core.matrix.utils.Constants.PROCESS_YEAR_REFERENCE;
import com.core.matrix.workflow.service.RuntimeActivitiService;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 *
 * @author thiag
 */
@Component
public class StartBillingJob {

    @Autowired
    private RuntimeActivitiService service;

    @Autowired
    private LogService logService;
    
    public void startBilling() {

        try {
            
            final LocalDate reference = LocalDate.now().minusMonths(1);
            Map<String, Object> variables = new HashMap<>();
            variables.put(PROCESS_MONTH_REFERENCE, reference.getMonthValue());
            variables.put(PROCESS_YEAR_REFERENCE, reference.getYear());
          

            
            Logger.getLogger(StartBillingJob.class.getName()).log(Level.INFO, "Start billing of all Contracts Matrix");
            
            //Start process for billing all contracts   
            //service.startProcessByMessage(PROCESS_BILLING_CONTRACT_MESSAGE_EVENT, variables);

            //Start process for cheking results of all contracts
                    
            service.startProcessByMessage(PROCESS_STATUS_PROCESS_FILE_MESSAGE_EVENT, variables);
            
           
        } catch (Exception e) {
            Logger.getLogger(StartBillingJob.class.getName()).log(Level.SEVERE, "[startBilling]", e);
            Log log = new Log();
            log.setMessage("StartBillingJob");
            log.setMessageErrorApplication(e.getLocalizedMessage());
            //logService.save(log);
        }

    }

}
