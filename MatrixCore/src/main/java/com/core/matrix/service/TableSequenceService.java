/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.service;

import com.core.matrix.model.TableSequence;
import com.core.matrix.repository.TableSequenceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author thiag
 */
@org.springframework.stereotype.Service
public class TableSequenceService {
    
    
    
    @Autowired
    private TableSequenceRepository repository;
    
    
    @Transactional
    public synchronized Long getValue(String seq, long size ){
        
        TableSequence sequence =  repository.findByName(seq).get();
        Long result = sequence.getValue();
        sequence.setValue(sequence.getValue() + size + 1);        
        repository.save(sequence);
        
        return result;
    }
    
    
    
    
}
