/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix;

import com.core.matrix.model.MeansurementFileDetail;
import com.core.matrix.service.MeansurementFileDetailService;
import com.core.matrix.utils.MeansurementFileDetailStatus;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 *
 * @author thiag
 */

@SpringBootTest
public class BatchTest {

    
    @Autowired
    private MeansurementFileDetailService detailService;
    
    
    @Test
    public void generateFile() {

        
        List<MeansurementFileDetail> details = new ArrayList<>();
        
        for (int i = 0; i < 38880; i++) {

            MeansurementFileDetail detail = new MeansurementFileDetail();

            detail.setAgent("TESTE");
            detail.setConsumptionActive(0D);
            detail.setConsumptionReactivate(0D);
            detail.setDate(LocalDate.now());
            detail.setEnergyType("TESTE");
            detail.setGenerationActive(0D);
            detail.setGenerationReactivate(0D);
            detail.setHour(1L);
            detail.setIdMeansurementFile(20019L);
            detail.setMeansurementPoint("POINT");
            detail.setNotificationCollection("");
            detail.setOrigem("origem");
            detail.setStatus(MeansurementFileDetailStatus.SUCCESS);            
            details.add(detail);
        }
        
        
        detailService.saveAllBatch(details);

    }
}
