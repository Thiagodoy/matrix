/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.resource;

import com.core.matrix.response.ProcessDefinitionResponse;
import com.core.matrix.workflow.service.RepositoryActivitiService;

import java.io.InputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author thiag
 */
@RestController
@RequestMapping(value = "/api/workflow/repository")
public class RepositoryResource {

    @Autowired
    private RepositoryActivitiService repositoryActivitiService;

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity delete(@PathVariable(value = "id", required = true) String id) {
        try {

            this.repositoryActivitiService.deleteProcessDefinition(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            Logger.getLogger(RepositoryResource.class.getName()).log(Level.SEVERE, "[delete]", e);
            return ResponseEntity.status(HttpStatus.resolve(500)).body(e.getMessage());
        }
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity get() {
        try {
            List<ProcessDefinitionResponse> response = this.repositoryActivitiService.listAll();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Logger.getLogger(RepositoryResource.class.getName()).log(Level.SEVERE, "[get]", e);
            return ResponseEntity.status(HttpStatus.resolve(500)).body(e.getMessage());
        }
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity post(@RequestPart(value = "file") MultipartFile file) {
        try {
            this.repositoryActivitiService.upload(file.getOriginalFilename(), file.getInputStream());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            Logger.getLogger(RepositoryResource.class.getName()).log(Level.SEVERE, "[post]", e);
            return ResponseEntity.status(HttpStatus.resolve(500)).body(e.getMessage());
        }
    }

    @RequestMapping(value = "/activate/{id}", method = RequestMethod.POST)
    public ResponseEntity postActivate(@PathVariable(value = "id", required = true) String id) {
        try {
            this.repositoryActivitiService.activateProcessDefinition(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            Logger.getLogger(RepositoryResource.class.getName()).log(Level.SEVERE, "[postActivate]", e);
            return ResponseEntity.status(HttpStatus.resolve(500)).body(e.getMessage());
        }
    }

    @RequestMapping(value = "/suspend/{id}", method = RequestMethod.POST)
    public ResponseEntity postSuspend(@PathVariable(value = "id", required = true) String id) {
        try {
            this.repositoryActivitiService.suspendProcessDefinition(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            Logger.getLogger(RepositoryResource.class.getName()).log(Level.SEVERE, "[postSuspend]", e);
            return ResponseEntity.status(HttpStatus.resolve(500)).body(e.getMessage());
        }
    }

    @RequestMapping(value = "/diagram", method = RequestMethod.GET)
    public ResponseEntity getDiagram(
            @RequestParam(name = "processDefinitionId", required = true) String processDefinitionId,
            @RequestParam(name = "processInstanceId", required = false) String processDefinitionInstance,
            HttpServletResponse response) {
        
        try {
            InputStream diagram = this.repositoryActivitiService.generateProcessDiagram(processDefinitionId, processDefinitionInstance);
            
            
            
            org.apache.commons.io.IOUtils.copy(diagram, response.getOutputStream());
            response.flushBuffer();            
            
            response.setContentType("image/png");
            response.setHeader("Content-disposition", "attachment; filename= diagram.png");
            
            return ResponseEntity.ok().build();
           
        } catch (Exception e) {
            Logger.getLogger(RepositoryResource.class.getName()).log(Level.SEVERE, "[getDiagram]", e);
            return ResponseEntity.status(HttpStatus.resolve(500)).body(e.getMessage());
        }
    }

}
