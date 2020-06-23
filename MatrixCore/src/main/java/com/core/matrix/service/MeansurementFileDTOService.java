/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.service;

import com.core.matrix.model.MeansurementFileDTO;
import com.core.matrix.repository.MeansurementFileDTORepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author thiag
 */
@Service
public class MeansurementFileDTOService {
    
    
    @Autowired
    private MeansurementFileDTORepository repository;
    
    @Transactional
    public void save(MeansurementFileDTO request){
        this.repository.save(request);
    }
    
    @Transactional
    public void saveAll(List<MeansurementFileDTO> request){
        this.repository.saveAll(request);
    }
    
    
    
    
    
    
}
