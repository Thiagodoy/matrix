/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.resource;

import com.core.matrix.model.ContractMeasurementPoint;
import com.core.matrix.service.ContractMeasurementPointService;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author thiag
 */
@RestController
@RequestMapping(value = "/api/associate/point")
public class ContractMeasurementPointResource {

    @Autowired
    private ContractMeasurementPointService service;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity associate(@RequestBody ContractMeasurementPoint request) {
        try {
            service.save(request);
            return ResponseEntity.ok().build();
        } catch (ConstraintViolationException e) {
            Logger.getLogger(ContractMeasurementPointResource.class.getName()).log(Level.SEVERE, "[associate]", e);
            return ResponseEntity.status(HttpStatus.resolve(500)).body("Ponto de medição já foi esta associado ao contrato!");
        } catch (Exception e) {
            Logger.getLogger(ContractMeasurementPointResource.class.getName()).log(Level.SEVERE, "[associate]", e);
            return ResponseEntity.status(HttpStatus.resolve(500)).body(e.getMessage());
        }
    }
}
