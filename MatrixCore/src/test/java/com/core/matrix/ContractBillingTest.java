/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix;

import com.core.matrix.wbc.dto.ContractDTO;
import com.core.matrix.wbc.service.ContractService;
import java.util.List;
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
    
    @Autowired
    private ContractService contractService;
    

    @Test
    public void getContracts() throws Exception{        
        List<ContractDTO> c = this.contractService.listForBilling(null);
    }


    
}
