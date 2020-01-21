/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.wbc.resource;

import com.core.matrix.wbc.model.MeansurementPoint;
import com.core.matrix.wbc.service.MeansurementPointService;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping(value = "/api/wbc/point-meansurement")
public class MeansurementPointResource {

    @Autowired
    private MeansurementPointService service;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity getPoint(@RequestParam(name = "point") String point) {
        try {
            MeansurementPoint response = service.getPoint(point);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Logger.getLogger(MeansurementPointResource.class.getName()).log(Level.SEVERE, "[getPoint]", e);
            return ResponseEntity.status(HttpStatus.resolve(500)).body(e.getMessage());
        }
    }

    @RequestMapping(value = "/check", method = RequestMethod.GET)
    public ResponseEntity checkPoint(@RequestParam(name = "point") String point) {
        try {

            boolean result = service.existsPoint(point);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Logger.getLogger(MeansurementPointResource.class.getName()).log(Level.SEVERE, "[checkPoint]", e);
            return ResponseEntity.status(HttpStatus.resolve(500)).body(e.getMessage());
        }
    }

}
