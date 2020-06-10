/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.resource;

import com.core.matrix.model.Price;
import com.core.matrix.service.PriceService;
import com.core.matrix.utils.Url;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
@RequestMapping(value = Url.URL_API_PRICE)
public class PriceResource extends Resource<Price,PriceService>{
    

    public PriceResource(PriceService service) {
        super(service);
    }   
    

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity get(
            @RequestParam(name = "id", required = false) Long id,
            @RequestParam(name = "description", required = false) String description,
            @RequestParam(name = "subMarket", required = false) Long subMarket,
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "size", required = false, defaultValue = "10") int size) {
        try {
            Page response = this.getService().find(id,subMarket,description,PageRequest.of(page, size, Sort.by("description")));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Logger.getLogger(PriceResource.class.getName()).log(Level.SEVERE, "[get]", e);
            return ResponseEntity.status(HttpStatus.resolve(500)).body(e.getMessage());
        }
    }

}
