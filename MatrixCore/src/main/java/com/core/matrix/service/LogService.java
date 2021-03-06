/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.service;

import com.core.matrix.model.Log;
import com.core.matrix.repository.LogRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author thiag
 */
@Service
public class LogService extends com.core.matrix.service.Service<Log, LogRepository>{

   

    public LogService(LogRepository repositoy) {
        super(repositoy);
    }

    
    @Transactional(readOnly = true)
    public Page<Log>listByProcessInstance(String id, Pageable page){
        return this.repository.findByProcessInstanceId(id, page);
    }
   
    
    @Transactional
    public void deleteLogsByFile(Long id) {
        this.repository.deleteByFileId(id);
    }
    
    @Transactional(transactionManager = "matrixTransactionManager")
    public void deleteLogsByProcessInstance(String id) {
        this.repository.deleteByprocessInstanceId(id);
    }

}
