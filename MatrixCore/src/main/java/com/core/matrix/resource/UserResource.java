/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.resource;

import com.core.matrix.request.UserDeleteRequest;
import com.core.matrix.workflow.model.UserActiviti;
import com.core.matrix.workflow.service.UserService;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
@RequestMapping(value = "/user")
public class UserResource {

    @Autowired
    private UserService service;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity get(@RequestParam(name = "firstName", required = false) String firstName,
            @RequestParam(name = "lastName",required = false) String lastName,
            @RequestParam(name = "email", required = false) String email,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        try {

            Page<UserActiviti> response = this.service.list(firstName, lastName, email, PageRequest.of(page, size));
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Logger.getLogger(UserResource.class.getName()).log(Level.SEVERE, "[get]", e.getMessage());
            return ResponseEntity.status(HttpStatus.resolve(500)).build();
        }
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity post(@RequestBody UserActiviti request) {
        try {

            this.service.save(request);
            return ResponseEntity.ok().build();

        } catch (Exception e) {
            Logger.getLogger(UserResource.class.getName()).log(Level.SEVERE, "[post]", e);
            return ResponseEntity.status(HttpStatus.resolve(500)).build();
        }
    }

    @RequestMapping(method = RequestMethod.DELETE)
    public ResponseEntity delete(@RequestBody UserDeleteRequest request) {
        try {

            this.service.delete(request);
            return ResponseEntity.ok().build();

        } catch (Exception e) {
            Logger.getLogger(UserResource.class.getName()).log(Level.SEVERE, "[delete]", e.getMessage());
            return ResponseEntity.status(HttpStatus.resolve(500)).build();
        }
    }

}
