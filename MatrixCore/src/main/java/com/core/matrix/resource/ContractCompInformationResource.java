/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.resource;

import com.core.matrix.model.ContractCompInformation;
import com.core.matrix.service.ContractCompInformationService;
import java.util.logging.Level;
import java.util.logging.Logger;
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
@RequestMapping(value = "/api/complement/information")
public class ContractCompInformationResource {
    
    
    @Autowired
    private ContractCompInformationService service;
    
    
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity post(@RequestBody ContractCompInformation request){
        try {
            this.service.save(request);
            return ResponseEntity.ok().build();
            
        } catch (Exception e) {
            Logger.getLogger(ContractCompInformationResource.class.getName()).log(Level.SEVERE, "[post]", e);
            return ResponseEntity.status(HttpStatus.resolve(500)).body(e.getMessage());
        }
    }
    
    
}
