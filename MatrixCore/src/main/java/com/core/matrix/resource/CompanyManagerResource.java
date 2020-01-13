/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.resource;

import com.core.matrix.model.CompanyManager;
import com.core.matrix.service.CompanyManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author thiag
 */
@RestController
@RequestMapping(value = "/api/company/manager")
public class CompanyManagerResource {

    @Autowired
    private CompanyManagerService service;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity associate(@RequestBody(required = true) CompanyManager companyManager) {
        try {
            service.associate(companyManager);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.resolve(500)).body(e.getMessage());
        }
    }
    
     @RequestMapping(value = "/{company}/{manager}",  method = RequestMethod.DELETE)
    public ResponseEntity associate(@PathVariable(name = "company", required = true)Long company, 
            @PathVariable(name = "manager", required = true)Long manager) {
        try {
            service.delete(company, manager);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.resolve(500)).body(e.getMessage());
        }
    }

}
