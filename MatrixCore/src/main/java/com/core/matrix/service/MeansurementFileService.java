/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.service;

import com.core.matrix.dto.MeansurementFileStatusDTO;
import com.core.matrix.model.MeansurementFile;
import com.core.matrix.repository.MeansurementFileRepository;
import com.core.matrix.utils.MeansurementFileStatus;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author thiag
 */
@Service
public class MeansurementFileService {
    
    
    @Autowired
    private MeansurementFileRepository repository;
    
    @Transactional 
    public MeansurementFile saveFile(MeansurementFile file){
        return this.repository.save(file);
    }
    
    
    @Transactional(transactionManager = "matrixTransactionManager")
    public List<MeansurementFile> findByProcessInstanceId(String id){
        return this.repository.findByProcessInstanceId(id);
    }
    
    @Transactional 
    public void delete(Long id){
         this.repository.deleteById(id);
    }
    
    @Transactional(transactionManager = "matrixTransactionManager")
    public void updateStatus(MeansurementFileStatus status, Long id){
        this.repository.updateStatus(status, id);
    }
    
    @Transactional(readOnly = true)
    public MeansurementFile findById(Long id) throws Exception{
        return this.repository.findById(id).orElseThrow(()-> new Exception("Arquivo não encontrado"));
    }
    
    @Transactional(readOnly = true)
    public List<MeansurementFileStatusDTO> getStatus(Long year, Long month){        
        
//        LocalDate referenceMonth = LocalDate.of(year, Month.of(month), 1);
//        LocalDateTime start = referenceMonth.atStartOfDay();
//        referenceMonth = referenceMonth.plusDays(Utils.getDaysOfMonth(referenceMonth));        
//        LocalDateTime end = referenceMonth.atStartOfDay();
        
        return this.repository.getStatus(year, month);
    }
    
    @Transactional(readOnly = true)
    public List<MeansurementFile>findAllFilesWithErrors(String processInstanceId){
        return this.repository.findAllFilesWithErrors(processInstanceId);
    }

    
}
