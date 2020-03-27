/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.resource;

import com.core.matrix.model.AuthorityApproval;
import com.core.matrix.service.AuthorityApprovalService;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author thiag
 */
@RestController
@RequestMapping(value = "/api/authority")
public class AuthorityApprovalResource {
    
    @Autowired
    private AuthorityApprovalService approvalService;   
    
    
    @RequestMapping(value = "/check",method = RequestMethod.GET)
    public ResponseEntity check(@RequestParam(name = "value")Double value){
        try {
            AuthorityApproval response = this.approvalService.findBetween(value);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Logger.getLogger(AuthorityApprovalResource.class.getName()).log(Level.SEVERE, "[check]", e);
            return ResponseEntity.status(HttpStatus.resolve(500)).body(e.getMessage());
        }
    }
    
    
}
