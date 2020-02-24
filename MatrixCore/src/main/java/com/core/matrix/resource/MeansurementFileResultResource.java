/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.resource;

import com.core.matrix.model.MeansurementFileResult;
import com.core.matrix.service.MeansurementFileResultService;
import java.util.List;
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
@RequestMapping(value = "/api/meansurement-file/result")
public class MeansurementFileResultResource {

    @Autowired
    private MeansurementFileResultService service;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity getResult(@RequestParam("processInstanceId") String processInstanceId) {
        try {

            List<MeansurementFileResult> response = this.service.getResult(processInstanceId);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Logger.getLogger(MeansurementFileResultResource.class.getName()).log(Level.SEVERE, "[getResult]", e.getMessage());
            return ResponseEntity.status(HttpStatus.resolve(500)).body(e.getMessage());
        }
    }

}
