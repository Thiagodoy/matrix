/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.workflow.task;

import com.core.matrix.model.MeansurementFile;
import com.core.matrix.service.MeansurementFileService;
import static com.core.matrix.utils.Constants.FILE_MEANSUREMENT_ID;
import java.time.Month;
import java.time.YearMonth;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author thiag
 */
public class DataValidationTask implements JavaDelegate {

    
    
    
    @Autowired
    private MeansurementFileService fileService;
    
    @Override
    public void execute(DelegateExecution de) throws Exception {

        Long id = de.getVariable(FILE_MEANSUREMENT_ID, Long.class);

        try {

            
            MeansurementFile file = fileService.findById(id);
            
            
            
            
            
            
            
        } catch (Exception e) {
        }

    }
    
    private void checkDays(MeansurementFile file){
        
        int daysOnMonth = YearMonth.of(file.getYear().intValue(), Month.of(file.getMonth().intValue())).lengthOfMonth();
        
        
       // Map<LocalDate,List<Object>> days = file.getDetails().stream().parallel().collect(Collectors.groupingBy(MeansurementFileDetail::getDate));
        
        
        
        
        
        
        
        
        
        
    }

}
