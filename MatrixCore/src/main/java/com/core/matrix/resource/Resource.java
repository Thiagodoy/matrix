/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.resource;

import com.core.matrix.model.Model;
import com.core.matrix.service.Service;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 *
 * @author thiag
 */
@Data
public abstract class Resource<T extends Model, S extends Service> {

    private S service;

    public Resource(S service) {
        this.service = service;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity post(@RequestBody T entity) {

        try {
            Long id = service.save(entity);
            return ResponseEntity.ok(id);
        } catch (Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.getMessage());
            return ResponseEntity.status(HttpStatus.resolve(500)).body(e.getMessage());
        }

    }

    @RequestMapping(value = "/collection", method = RequestMethod.POST)
    public ResponseEntity postAll(@RequestBody List<T> entity) {

        try {
            List<Long> id = service.save(entity);
            return ResponseEntity.ok(id);
        } catch (Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.getMessage());
            return ResponseEntity.status(HttpStatus.resolve(500)).body(e.getMessage());
        }

    }

    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity put(@RequestBody T entity) throws Throwable {
        try {
            service.update(entity);
            return ResponseEntity.ok().build();

        } catch (Exception e) {

            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.getMessage());
            return ResponseEntity.status(HttpStatus.resolve(500)).body(e.getMessage());
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity delete(@PathVariable(name = "id") Long id) {

        try {

            service.delete(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {

            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.getMessage());
            return ResponseEntity.status(HttpStatus.resolve(500)).body(e.getMessage());
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity getById(@PathVariable(name = "id") Long id) throws Throwable {

        try {
            return ResponseEntity.ok(this.service.find(id));
        } catch (Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.getMessage());
            return ResponseEntity.status(HttpStatus.resolve(500)).body(e.getMessage());
        }
    }

}
