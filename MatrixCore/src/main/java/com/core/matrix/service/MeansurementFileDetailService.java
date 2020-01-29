/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.service;

import com.core.matrix.model.MeansurementFileDetail;
import com.core.matrix.repository.MeansurementFileDetailRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author thiag
 */
@Service
public class MeansurementFileDetailService {
    
    
    
    @Autowired
    private MeansurementFileDetailRepository repository;
    
    @Transactional
    public void save(MeansurementFileDetail detail){
        this.repository.save(detail);
    }
    
    @Transactional
    public void save(List<MeansurementFileDetail> detail){
        this.repository.saveAll(detail);
    }
    
    
}
