/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.jobs;

import com.core.matrix.wbc.dto.ContractDTO;
import com.core.matrix.wbc.service.ContractService;
import java.util.List;
import java.util.stream.Collectors;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 *
 * @author thiag
 */
public class BillingContractsJob extends QuartzJobBean {

    @Autowired
    private ContractService contractService;

    @Override
    protected void executeInternal(JobExecutionContext jec) throws JobExecutionException {

        try {
            List<ContractDTO> contracts = this.contractService.listForBilling();

            //Contracts without rateio
            List<ContractDTO> contractsWithoutRateio = contracts
                    .parallelStream()
                    .filter(contract -> contract.getBFlRateio().equals(1L))
                    .collect(Collectors.toList());
            
            
            
            

        } catch (Exception e) {
        }

    }
    
    private void createAProcessForBilling(List<ContractDTO>contracts){
        
        
        
        
    }

}
