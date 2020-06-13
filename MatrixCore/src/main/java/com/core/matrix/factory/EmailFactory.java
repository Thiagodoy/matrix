/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.factory;

import com.core.matrix.model.Email;
import com.core.matrix.model.Template;
import com.core.matrix.service.TemplateService;
import com.core.matrix.specifications.TemplateSpecification;
import com.core.matrix.utils.Constants;
import com.core.matrix.utils.Utils;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

/**
 *
 * @author thiag
 */
@Component
public class EmailFactory {

    @Autowired
    private TemplateService templateService;

    public Email createEmailTemplate(Template.TemplateBusiness templateBusiness) {

        try {
            Specification spc = TemplateSpecification.filter(null, null, null, templateBusiness);
            Template template = (Template) templateService.find(spc, Pageable.unpaged()).getContent().get(0);
            Email email = new Email();
            email.setTemplate(template);
            email.setMapData(new HashMap<String, String>());
            return email;
        } catch (Exception ex) {
            Logger.getLogger(EmailFactory.class.getName()).log(Level.SEVERE, "[createEmailTemplate]", ex);
            throw new RuntimeException("Erro ao criar o email!");
        }
    }

}
