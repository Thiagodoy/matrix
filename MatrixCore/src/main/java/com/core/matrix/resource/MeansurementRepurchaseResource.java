/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.resource;

import com.core.matrix.model.MeansurementRepurchase;
import com.core.matrix.service.MeansurementRepurchaseService;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author thiag
 */
@RestController
@RequestMapping(value = "/api/repurchase")
public class MeansurementRepurchaseResource {

    @Autowired
    private MeansurementRepurchaseService service;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity post(@RequestBody MeansurementRepurchase request) {
        try {
            this.service.save(request);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            Logger.getLogger(MeansurementRepurchaseResource.class.getName()).log(Level.SEVERE, "[post]", e);
            return ResponseEntity.status(HttpStatus.resolve(500)).body(e.getMessage());
        }
    }

    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity put(@RequestBody MeansurementRepurchase request) {
        try {
            this.service.update(request);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            Logger.getLogger(MeansurementRepurchaseResource.class.getName()).log(Level.SEVERE, "[put]", e);
            return ResponseEntity.status(HttpStatus.resolve(500)).body(e.getMessage());
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity delete(@PathVariable("id") Long id) {
        try {
            this.service.delete(id);
            return ResponseEntity.ok().build();

        } catch (Exception e) {
            Logger.getLogger(MeansurementRepurchaseResource.class.getName()).log(Level.SEVERE, "[delete]", e);
            return ResponseEntity.status(HttpStatus.resolve(500)).body(e.getMessage());
        }
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity get(
            @RequestParam(name = "id", required = false) Long id,
            @RequestParam(name = "idMeansurementFile", required = false) Long idMeansurementFile,
            @RequestParam(name = "processInstanceId", required = false) String processInstanceId,
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "size", required = false, defaultValue = "10") int size) {
        try {
            Page response = this.service.find(id, idMeansurementFile, processInstanceId, PageRequest.of(page, size));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Logger.getLogger(MeansurementRepurchaseResource.class.getName()).log(Level.SEVERE, "[get]", e);
            return ResponseEntity.status(HttpStatus.resolve(500)).body(e.getMessage());
        }
    }

}
