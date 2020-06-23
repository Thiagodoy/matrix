/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.service;

import com.core.matrix.model.ProcessStatusLote;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.core.matrix.repository.ProcessStatusLoteRepository;

/**
 *
 * @author thiag
 */
@Service
public class ProcessStatusLoteService {    
    
    @Autowired
    private ProcessStatusLoteRepository repository;
    
    @Transactional
    public void save(ProcessStatusLote request){
        this.repository.save(request);
    }
    
    @Transactional
    public void saveAll(List<ProcessStatusLote> request){
        this.repository.saveAll(request);
    }
    
}
