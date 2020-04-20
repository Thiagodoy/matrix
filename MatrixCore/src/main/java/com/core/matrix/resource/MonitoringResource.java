/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.resource;

import com.core.matrix.repository.MonitoringRepository;
import com.core.matrix.specifications.MonitoringSpecification;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping(value = "/api/monitoring")
public class MonitoringResource {

    @Autowired
    private MonitoringRepository monitoringRepository;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity get(@RequestParam(required = false, name = "status") String status,
            @RequestParam(required = false, name = "instanciaDoProcesso") String instanciaDoProcesso,
            @RequestParam(required = false, name = "wbcContrato") String wbcContrato, 
            @RequestParam(required = false, name = "pontoMedicao") String pontoMedicao,
            @RequestParam(required = false, name = "empresa") String empresa,
            @RequestParam(required = true, name = "ano") String ano,
            @RequestParam(required = true, name = "mes") String mes,
            @RequestParam(name = "page", defaultValue = "0")int page,
            @RequestParam(name = "size", defaultValue = "10")int size) {

        try {
            
            Specification spc =  MonitoringSpecification.parameters(status, instanciaDoProcesso, wbcContrato, pontoMedicao, empresa, ano, mes);            
            Page response = monitoringRepository.findAll(spc, PageRequest.of(page, size, Sort.by("instanciaDoProcesso").ascending()));            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Logger.getLogger(MonitoringResource.class.getName()).log(Level.SEVERE, "[get]", e);
            return ResponseEntity.status(HttpStatus.resolve(500)).body(e.getMessage());
        }

    }

}
