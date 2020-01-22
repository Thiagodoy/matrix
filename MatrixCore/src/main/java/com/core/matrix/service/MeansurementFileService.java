/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.service;

import com.core.matrix.model.MeansurementFile;
import com.core.matrix.repository.MeansurementFileRepository;
import com.core.matrix.utils.MeansurementFileStatus;
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
    public void saveFile(MeansurementFile file){
        this.repository.save(file);
    }
    
    @Transactional
    public void updateStatus(MeansurementFileStatus status, Long id){
        this.repository.updateStatus(status, id);
    }
    
    @Transactional(readOnly = true)
    public MeansurementFile findById(Long id) throws Exception{
        return this.repository.findById(id).orElseThrow(()-> new Exception("Arquivo não encontrado"));
    }

    
}
