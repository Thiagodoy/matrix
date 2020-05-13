/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.resource;

import com.core.matrix.model.Template;
import com.core.matrix.service.TemplateService;
import static com.core.matrix.utils.Url.URL_API_TEMPLATE;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author thiag
 */
@RestController
@RequestMapping(value = URL_API_TEMPLATE)
public class TemplateResource extends Resource<Template, TemplateService>{    
    public TemplateResource(TemplateService service) {
        super(service);
    }    
}
