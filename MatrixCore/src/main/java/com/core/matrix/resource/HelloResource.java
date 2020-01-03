/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.resource;

import java.security.Principal;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author thiag
 */
@RestController
@RequestMapping(value = "/hello")
public class HelloResource {
    
    
    
    
    
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity get(Principal principal){
        
        return ResponseEntity.ok("Hello and user connected ->" + principal.getName() );
    }
    
    
    
    
}
