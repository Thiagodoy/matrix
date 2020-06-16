/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.resource;

import com.core.matrix.model.MeansurementFile;
import com.core.matrix.response.FileStatusBillingResponse;
import com.core.matrix.service.MeansurementFileService;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
@RequestMapping(value = "/api/file")
public class MeansurementFileResource {
    
    
    @Autowired
    private MeansurementFileService service;

    
    @RequestMapping(value = "/status",method = RequestMethod.GET)
    public ResponseEntity status(
            @RequestParam("year") Long year, 
            @RequestParam("month") Long month,
            @RequestParam(name = "loadSummary", defaultValue = "false") boolean loadSummary,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        try {
             FileStatusBillingResponse response = this.service.status(month, year, loadSummary,PageRequest.of(page, size, Sort.by("nickname")));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Logger.getLogger(MeansurementFileResource.class.getName()).log(Level.SEVERE, "[ status ]", e.getMessage());
            return ResponseEntity.status(HttpStatus.resolve(500)).body(e);
        }
    }
    
    
    
    @RequestMapping(value = "/{id}",method = RequestMethod.DELETE)
    public ResponseEntity delete(@PathVariable(name = "id") Long id) {
        try {
            this.service.delete(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            Logger.getLogger(MeansurementFileResource.class.getName()).log(Level.SEVERE, "[ delete ]", e.getMessage());
            return ResponseEntity.status(HttpStatus.resolve(500)).body(e);
        }
    }
    
    
    @RequestMapping(value = "/errors", method = RequestMethod.GET)
    public ResponseEntity getFilesWithErrors(@RequestParam(name = "processInstanceId",required = true) String processInstanceId){
        try {
            List<MeansurementFile> response = this.service.findAllFilesWithErrors(processInstanceId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Logger.getLogger(MeansurementFileResource.class.getName()).log(Level.SEVERE, "[ getByProcessInstanceId ]", e.getMessage());
            return ResponseEntity.status(HttpStatus.resolve(500)).body(e);
        }
    }
    
}
