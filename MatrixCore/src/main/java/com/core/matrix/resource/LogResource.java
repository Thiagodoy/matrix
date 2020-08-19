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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
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
    public ResponseEntity getErrors(@RequestParam("processInstanceId") String processInstance,
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "size", required = false, defaultValue = "30") int size) {
        try {
            Page<Log>response = this.service.listByProcessInstance(processInstance, PageRequest.of(page, size, Sort.by("id").ascending()));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Logger.getLogger(LogResource.class.getName()).log(Level.SEVERE, "[getErrors]", e);
            return ResponseEntity.status(HttpStatus.resolve(500)).body(e.getMessage());
        }
    }
    
     @RequestMapping(value = "/{id}",method = RequestMethod.DELETE)
    public ResponseEntity deleteByProcess(@PathVariable("id") String processInstance) {
        try {
            this.service.deleteLogsByProcessInstance(processInstance);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            Logger.getLogger(LogResource.class.getName()).log(Level.SEVERE, "[deleteByProcess]", e);
            return ResponseEntity.status(HttpStatus.resolve(500)).body(e.getMessage());
        }
    }

}
