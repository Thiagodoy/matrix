/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.resource;

import com.core.matrix.specifications.GroupSpecification;
import com.core.matrix.workflow.model.GroupActiviti;
import com.core.matrix.workflow.service.GroupActivitiService;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
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
@RequestMapping(value = "/api/group")
public class GroupResource {

    @Autowired
    private GroupActivitiService service;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity get(
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        
        
        
                
        
            Specification spc = GroupSpecification.name(name);
                    
        
        try {
            Page<GroupActiviti> response = this.service.listAll(spc, PageRequest.of(page, size, Sort.by("name")));
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Logger.getLogger(GroupResource.class.getName()).log(Level.SEVERE, "[get]", e);
            return ResponseEntity.status(HttpStatus.resolve(500)).build();
        }
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity post(@RequestBody GroupActiviti body) {
        try {

            this.service.save(body);
            return ResponseEntity.ok().build();

        } catch (Exception e) {
            Logger.getLogger(GroupResource.class.getName()).log(Level.SEVERE, "[post]", e);
            return ResponseEntity.status(HttpStatus.resolve(500)).build();
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity delete(@PathVariable(name = "id") String id) {

        try {
            this.service.delete(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            Logger.getLogger(GroupResource.class.getName()).log(Level.SEVERE, "[delete]", e);
            return ResponseEntity.status(HttpStatus.resolve(500)).build();
        }
    }

}
