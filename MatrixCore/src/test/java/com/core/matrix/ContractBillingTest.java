/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix;

import com.core.matrix.workflow.task.BillingContractsTask;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author thiag
 */
@SpringBootTest
public class ContractBillingTest {  
    
    
    @Autowired
    private ApplicationContext context;
    

    @Test
    public void getContracts() throws Exception{
        
        BillingContractsTask billingContractsTask = new BillingContractsTask(context);
        
        BillingContractsTask task = new BillingContractsTask();
        task.execute(null);
    }


    
}
