/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.resource;

import com.core.matrix.model.Parameters;
import com.core.matrix.service.ParametersService;
import com.core.matrix.specifications.ParametersSpecification;
import static com.core.matrix.utils.Url.URL_API_PARAMETERS;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
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
@RequestMapping(value = URL_API_PARAMETERS)
public class ParametersResource extends Resource<Parameters, ParametersService> {

    public ParametersResource(ParametersService service) {
        super(service);
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity get(
            @RequestParam(name = "key", required = false) String key,
            @RequestParam(name = "value", required = false) String value,
            @RequestParam(name = "description", required = false) String description,
            @RequestParam(name = "type", required = false) String type,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        try {

            Specification spc = ParametersSpecification.find(key, value, description, type, Boolean.FALSE);
            Page response = this.getService().find(spc, PageRequest.of(page, size, Sort.by("key").ascending()));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.getMessage());
            return ResponseEntity.status(HttpStatus.resolve(500)).body(e.getMessage());
        }
    }

}
