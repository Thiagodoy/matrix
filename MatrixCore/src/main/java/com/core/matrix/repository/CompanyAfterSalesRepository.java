/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.repository;

import com.core.matrix.model.CompanyAfterSales;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author thiag
 */
public interface CompanyAfterSalesRepository extends JpaRepository<CompanyAfterSales, CompanyAfterSales.IdClass>{
    
}
