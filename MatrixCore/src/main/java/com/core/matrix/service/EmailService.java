/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.service;

import com.core.matrix.model.Email;
import com.core.matrix.repository.EmailRepository;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author thiag
 */
@org.springframework.stereotype.Service
public class EmailService extends Service<Email, EmailRepository> {

    @Autowired
    private TemplateService templateService;

    public EmailService(EmailRepository repositoy) {
        super(repositoy);
    }

    

}
