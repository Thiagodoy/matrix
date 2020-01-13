/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.service;

import com.core.matrix.model.CompanyManager;
import com.core.matrix.repository.CompanyManagerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author thiag
 */

@Service
public class CompanyManagerService {
    
    @Autowired
    private CompanyManagerRepository repository;    
    
    @Transactional
    public void associate(CompanyManager companyManager){
        this.repository.save(companyManager);
    }
    
    @Transactional
    public void delete(Long company, Long manager){
        
        CompanyManager.IdClass id = new CompanyManager.IdClass();
        id.setEmpresa(company);
        id.setManager(manager);
        this.repository.deleteById(id);        
    }
    
}
