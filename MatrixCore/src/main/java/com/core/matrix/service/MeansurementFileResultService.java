/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.service;

import com.core.matrix.model.MeansurementFileResult;
import com.core.matrix.repository.MeansurementFileResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author thiag
 */
@Service
public class MeansurementFileResultService {
    
    @Autowired
    private MeansurementFileResultRepository repository;
    
    
    @Transactional 
    public void save(MeansurementFileResult result){
        this.repository.save(result);
    }
    
    
}
