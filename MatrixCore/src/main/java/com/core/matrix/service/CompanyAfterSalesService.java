/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.service;

import com.core.matrix.model.CompanyAfterSales;
import com.core.matrix.repository.CompanyAfterSalesRepository;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
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
    public void save(CompanyAfterSales request) {
        this.repository.save(request);
    }

    @Transactional
    public void update(CompanyAfterSales request) {

        Optional<CompanyAfterSales> opt = this.repository.findByCompanyIn(Arrays.asList(request.getCompany())).stream().findFirst();

        if (opt.isPresent()) {
            CompanyAfterSales afterSales = opt.get();
            this.delete(afterSales.getCompany(), afterSales.getUser());
        }

        this.repository.save(request);
    }

    @Transactional
    public void delete(Long company, String user) {
        CompanyAfterSales.IdClass id = new CompanyAfterSales.IdClass();
        id.setCompany(company);
        id.setUser(user);
        this.repository.deleteById(id);
    }

    @Transactional
    public void delete(Long company) {

        Optional<CompanyAfterSales> opt = this.findByCodCompany(Arrays.asList(company)).stream().findFirst();

        if (opt.isPresent()) {
            CompanyAfterSales ss = opt.get();
            CompanyAfterSales.IdClass id = new CompanyAfterSales.IdClass();
            id.setCompany(ss.getCompany());
            id.setUser(ss.getUser());
            this.repository.deleteById(id);
        }

    }

    @Transactional(readOnly = true)
    public List<CompanyAfterSales> findByCodCompany(List<Long> codes) {
        return this.repository.findByCompanyIn(codes);
    }

}
