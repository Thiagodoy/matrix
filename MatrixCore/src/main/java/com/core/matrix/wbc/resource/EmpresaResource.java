/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.wbc.resource;

import com.core.matrix.wbc.model.Empresa;
import com.core.matrix.wbc.service.EmpresaService;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
@RequestMapping(value = "/wbc/empresa")
public class EmpresaResource {

    @Autowired
    private EmpresaService service;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity get(
            @RequestParam(name = "cnpj", required = false) String cnpj,
            @RequestParam(name = "razaoSocial", required = false) String razaoSocial,
            @RequestParam(name = "page", required = true) Long page,
            @RequestParam(name = "size", required = true) Long size) {

        try {

            Page<Empresa> response = service.findAll(cnpj,razaoSocial,PageRequest.of(page.intValue(), size.intValue()));
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Logger.getLogger(EmpresaResource.class.getName()).log(Logger.Level.FATAL, "[get]", e);
            return ResponseEntity.status(HttpStatus.resolve(500)).body(e.getMessage());
        }

    }

}
