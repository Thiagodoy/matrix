/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.service;

import com.core.matrix.model.Template;
import com.core.matrix.repository.TemplateRepository;

/**
 *
 * @author thiag
 */
@org.springframework.stereotype.Service
public class TemplateService extends Service<Template, TemplateRepository>{
    
    public TemplateService(TemplateRepository repositoy) {
        super(repositoy);
    }
    
}
