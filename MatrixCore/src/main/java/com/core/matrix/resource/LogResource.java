/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.resource;

import com.core.matrix.model.Log;
import com.core.matrix.service.LogService;
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
@RequestMapping(value = "/api/log")
public class LogResource {

    @Autowired
    private LogService service;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity getErrors(@RequestParam("processInstanceId") String processInstance) {
        try {
            List<Log>response = this.service.listByProcessInstance(processInstance);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Logger.getLogger(LogResource.class.getName()).log(Level.SEVERE, "[getErrors]", e);
            return ResponseEntity.status(HttpStatus.resolve(500)).body(e.getMessage());
        }
    }

}
