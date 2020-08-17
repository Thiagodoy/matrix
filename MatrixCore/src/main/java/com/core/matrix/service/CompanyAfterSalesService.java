/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.service;

import com.core.matrix.model.CompanyAfterSales;
import com.core.matrix.repository.CompanyAfterSalesRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author thiag
 */
@Service
public class CompanyAfterSalesService {
    
    
    @Autowired
    private CompanyAfterSalesRepository repository;
    
    @Transactional
    public void save(CompanyAfterSales request){
        this.repository.save(request);
    }
    
    @Transactional
    public void delete(Long company, String user){
        CompanyAfterSales.IdClass id = new CompanyAfterSales.IdClass();
        id.setCompany(company);
        id.setUser(user);
        this.repository.deleteById(id);
    }
    
    
    @Transactional
    public List<CompanyAfterSales> findByCodCompany(List<Long> codes){
        return this.repository.findByCompanyIn(codes);
    }
    
    
    
    
}
