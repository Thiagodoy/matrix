/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.resource;

import com.core.matrix.model.CompanyAfterSales;
import com.core.matrix.service.CompanyAfterSalesService;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author thiag
 */
@RestController
@RequestMapping(value = "/api/company/aftersales")
public class CompanyAfterSalesResource {
    
    
    @Autowired
    private CompanyAfterSalesService afterSalesService;
    
    
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity associate(@RequestBody(required = true) CompanyAfterSales body){
        try {
            afterSalesService.save(body);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            Logger.getLogger(CompanyAfterSales.class.getName()).log(Logger.Level.FATAL,"[associate]", e);
            return ResponseEntity.status(HttpStatus.resolve(500)).body(e.getMessage());
        }
    }
}
