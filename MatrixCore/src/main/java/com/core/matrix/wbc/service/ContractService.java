/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.wbc.service;

import com.core.matrix.wbc.repository.ContractRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author thiag
 */
@Service
public class ContractService {

    @Autowired
    private ContractRepository repository;

    @Transactional(readOnly = true)
    public Page findAll(Long contractId, PageRequest page) {
        return this.repository.shortInfomation(contractId, page);
    }
    
    @Transactional(readOnly = true)
    public Long countContract(){
        return this.repository.countContract();
    }
}
