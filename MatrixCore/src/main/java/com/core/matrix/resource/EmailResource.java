/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.resource;

import com.core.matrix.model.Email;
import com.core.matrix.service.EmailService;
import static com.core.matrix.utils.Url.URL_API_NOTIFICATION;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author thiag
 */
@RestController
@RequestMapping(value = URL_API_NOTIFICATION)
public class EmailResource extends Resource<Email, EmailService>{    
    public EmailResource(EmailService service) {
        super(service);
    }    
}
