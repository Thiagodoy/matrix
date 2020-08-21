/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.resource;

import com.core.matrix.model.Template;
import com.core.matrix.repository.TemplateRepository;
import com.core.matrix.service.TemplateService;

import static com.core.matrix.utils.Url.URL_API_TEMPLATE;
import static org.springframework.http.ResponseEntity.*;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

/**
 * @author thiag
 * @author diego <diego.lima@bandtec.com.br>
 */
@RestController
@RequestMapping(value = URL_API_TEMPLATE)
public class TemplateResource extends Resource<Template, TemplateService> {

    public TemplateResource(TemplateService service) {
        super(service);
    }
}
