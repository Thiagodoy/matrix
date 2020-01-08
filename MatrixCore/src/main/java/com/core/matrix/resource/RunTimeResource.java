/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.resource;

import com.core.matrix.request.CompleteTaskRequest;
import com.core.matrix.request.StartProcessRequest;
import com.core.matrix.response.TaskResponse;
import com.core.matrix.utils.Constants;
import com.core.matrix.workflow.service.RuntimeActivitiService;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping(value = "/workflow/runtime")
public class RunTimeResource {

    @Autowired
    private RuntimeActivitiService service;

    @RequestMapping(value = "/startProcess", method = RequestMethod.POST)
    public ResponseEntity startProcess(@RequestBody StartProcessRequest request, Principal principal) {
        try {
            Optional<TaskResponse> response = this.service.startProcess(request, principal.getName());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Logger.getLogger(RunTimeResource.class.getName()).log(Level.SEVERE, "[startProcess]", e);
            return ResponseEntity.status(HttpStatus.resolve(500)).body(e.getMessage());
        }
    }

    
    @RequestMapping(value = "/getTask", method = RequestMethod.POST)
    public ResponseEntity getTask(@RequestParam(value = "taskId", required = true) String taskId) {
        try {
            TaskResponse response = this.service.getTask(taskId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Logger.getLogger(RunTimeResource.class.getName()).log(Level.SEVERE, "[getTask]", e);
            return ResponseEntity.status(HttpStatus.resolve(500)).body(e.getMessage());
        }
    }
    
    
    @RequestMapping(value = "/completeTask", method = RequestMethod.POST)
    public ResponseEntity completeTask(@RequestBody CompleteTaskRequest request, Principal principal) {
        try {
            Optional<TaskResponse> response = this.service.completeTask(principal.getName(),request);
            
            if(response.isPresent()){
                return ResponseEntity.ok(response.get());
            }else{
                return ResponseEntity.ok().build();
            }
            
            
        } catch (Exception e) {
            Logger.getLogger(RunTimeResource.class.getName()).log(Level.SEVERE, "[getTask]", e);
            return ResponseEntity.status(HttpStatus.resolve(500)).body(e.getMessage());
        }
    }

    //TODO Create a pagination
    @RequestMapping(value = "/getCandidateTask", method = RequestMethod.GET)
    public ResponseEntity getCandidateTask(Principal principal) {
        try {
            List<TaskResponse> response = this.service.getCandidateTasks(principal.getName());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Logger.getLogger(RunTimeResource.class.getName()).log(Level.SEVERE, "[getCandidateTask]", e);
            return ResponseEntity.status(HttpStatus.resolve(500)).body(e.getMessage());
        }
    }

    @RequestMapping(value = "/getMyTask", method = RequestMethod.GET)
    public ResponseEntity getMyTask(Principal principal) {
        try {
            List<TaskResponse> response = this.service.getMyTasks(principal.getName());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Logger.getLogger(RunTimeResource.class.getName()).log(Level.SEVERE, "[getMyTask]", e);
            return ResponseEntity.status(HttpStatus.resolve(500)).body(e.getMessage());
        }
    }
    
    @RequestMapping(value = "/getInvolvedTasks", method = RequestMethod.GET)
    public ResponseEntity getInvolvedTasks(Principal principal) {
        try {
            List<TaskResponse> response = this.service.getInvolvedTasks(principal.getName());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Logger.getLogger(RunTimeResource.class.getName()).log(Level.SEVERE, "[getInvolvedTasks]", e);
            return ResponseEntity.status(HttpStatus.resolve(500)).body(e.getMessage());
        }
    }
    
    @RequestMapping(value = "/getGroupTasks", method = RequestMethod.GET)
    public ResponseEntity getGroupTasks(Principal principal) {
        try {
            List<TaskResponse> response = this.service.getGroupTasks(principal.getName());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Logger.getLogger(RunTimeResource.class.getName()).log(Level.SEVERE, "[getGroupTasks]", e);
            return ResponseEntity.status(HttpStatus.resolve(500)).body(e.getMessage());
        }
    }
    
    
    
    
    
    
    
    
    
    

    @RequestMapping(value = "/variables", method = RequestMethod.GET)
    public ResponseEntity getVariables(@RequestParam(value = "type", required = true) String type,
            @RequestParam(value = "processInstanceId", required = false) String processInstanceId,
            @RequestParam(value = "taskId", required = false) String taskId) {
        try {
            Map<String, Object> response = type.equals(Constants.TASK_VARIABLE)
                    ? this.service.getLocalVariables(taskId)
                    : this.service.getVariables(processInstanceId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Logger.getLogger(RunTimeResource.class.getName()).log(Level.SEVERE, "[getVariables]", e);
            return ResponseEntity.status(HttpStatus.resolve(500)).body(e.getMessage());
        }
    }

}
