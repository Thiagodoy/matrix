/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.resource;

import com.core.matrix.model.Email;
import com.core.matrix.service.EmailService;
import com.core.matrix.utils.ThreadPoolEmail;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import static com.core.matrix.utils.Url.URL_API_EMAIL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 *
 * @author thiag
 */
@RestController
@RequestMapping(value = URL_API_EMAIL)
public class EmailResource extends Resource<Email, EmailService>{    
    
    @Autowired
    private ThreadPoolEmail threadPoolEmail;
    
    public EmailResource(EmailService service) {
        super(service);
    }   
    
    
    @RequestMapping(value = "/sendEmail", method = RequestMethod.POST)
    public ResponseEntity sendEmail(@RequestBody Email email ){
        try {
            
            this.threadPoolEmail.submit(email);
            return ResponseEntity.ok().build();
            
        } catch (Exception e) {
            Logger.getLogger(EmailResource.class.getName()).log(Level.SEVERE, "[sendEmail]",e);
            return ResponseEntity.status(HttpStatus.resolve(500)).body(e.getMessage());
        }
    }
    
}
