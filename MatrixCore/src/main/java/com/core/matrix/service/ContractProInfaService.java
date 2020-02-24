/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.service;

import com.core.matrix.model.ContractProInfa;
import com.core.matrix.repository.ContractProInfaRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author thiag
 */
@Service
public class ContractProInfaService {

    @Autowired
    private ContractProInfaRepository repository;

    @Transactional
    public void delete(Long id) {
        this.repository.deleteById(id);
    }

    @Transactional
    public void saveAll(List<ContractProInfa> list) {
        this.repository.saveAll(list);
    }
    
    

}
