/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.resource;

import com.core.matrix.model.ContactManager;
import com.core.matrix.request.ContactManagerRequest;
import com.core.matrix.service.ContactManagerService;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
@RequestMapping(value = "/api/manager/contact")
public class ContactManagerResource {

    @Autowired
    private ContactManagerService contactManagerService;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity post(@RequestBody ContactManagerRequest request) {
        try {
            Long id  = contactManagerService.save(request);
            return ResponseEntity.ok(id);
        } catch (Exception e) {
            Logger.getLogger(ContactManagerResource.class.getName()).log(Logger.Level.FATAL, "[post]", e);
            return ResponseEntity.status(HttpStatus.resolve(500)).body(e.getMessage());
        }
    }

    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity put(@RequestBody ContactManagerRequest request) {
        try {
            contactManagerService.update(request);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            Logger.getLogger(ContactManagerResource.class.getName()).log(Logger.Level.FATAL, "[put]", e);
            return ResponseEntity.status(HttpStatus.resolve(500)).body(e.getMessage());
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity delete(@PathVariable(name = "id") Long id) {
        try {
            contactManagerService.delete(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            Logger.getLogger(ContactManagerResource.class.getName()).log(Logger.Level.FATAL, "[delete]", e);
            return ResponseEntity.status(HttpStatus.resolve(500)).body(e.getMessage());
        }
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity get(@RequestParam(name = "managerId", required = false) Long managerId,
            @RequestParam(name = "page", required = true, defaultValue = "0") int page,
            @RequestParam(name = "size", required = true, defaultValue = "10") int size) {

        try {
            Page<ContactManager> response = contactManagerService.find(managerId, PageRequest.of(page, size, Sort.by("name").descending()));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Logger.getLogger(ContactManagerResource.class.getName()).log(Logger.Level.FATAL, "[get]", e);
            return ResponseEntity.status(HttpStatus.resolve(500)).body(e.getMessage());
        }

    }

}
