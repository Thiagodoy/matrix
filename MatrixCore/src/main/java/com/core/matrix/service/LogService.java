/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.service;

import com.core.matrix.model.Log;
import com.core.matrix.repository.LogRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author thiag
 */
@Service
public class LogService {

    @Autowired
    private LogRepository repository;

    
    @Transactional(readOnly = true)
    public List<Log>listByProcessInstance(String id){
        return this.repository.findByActIdProcesso(id);
    }
    
    
    @Transactional
    public void save(Log log) {
        this.repository.save(log);
    }    
    
    @Transactional
    public void save(List<Log> log) {
        this.repository.saveAll(log);
    }
    
    @Transactional
    public void deleteLogsByFile(Long id) {
        this.repository.deleteByFileId(id);
    }
    
    @Transactional(transactionManager = "matrixTransactionManager")
    public void deleteLogsByProcessInstance(String id) {
        this.repository.deleteByActIdProcesso(id);
    }

}
