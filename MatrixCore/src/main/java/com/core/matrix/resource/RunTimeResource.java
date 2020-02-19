/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.resource;

import com.core.matrix.request.AddComment;
import com.core.matrix.request.CompleteTaskRequest;
import com.core.matrix.request.StartProcessRequest;
import com.core.matrix.response.AttachmentResponse;
import com.core.matrix.response.PageResponse;
import com.core.matrix.response.ProcessDetailResponse;
import com.core.matrix.response.TaskResponse;
import com.core.matrix.utils.Constants;
import com.core.matrix.workflow.model.UserActiviti;
import com.core.matrix.workflow.service.RuntimeActivitiService;
import java.io.InputStream;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletResponse;
import org.activiti.engine.task.Attachment;
import org.activiti.engine.task.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
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
@RequestMapping(value = "/api/workflow/runtime")
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
    
     @RequestMapping(value = "/startProcessByMessage", method = RequestMethod.POST)
    public ResponseEntity startProcessByMessage(@RequestParam("message") String message) {
        try {
            String response = this.service.startProcessByMessage(message);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Logger.getLogger(RunTimeResource.class.getName()).log(Level.SEVERE, "[startProcessByMessage]", e);
            return ResponseEntity.status(HttpStatus.resolve(500)).body(e.getMessage());
        }
    }

    @RequestMapping(value = "/getTask", method = RequestMethod.GET)
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
            Optional<TaskResponse> response = this.service.completeTask(principal.getName(), request);

            if (response.isPresent()) {
                return ResponseEntity.ok(response.get());
            } else {
                return ResponseEntity.ok().build();
            }

        } catch (Exception e) {
            Logger.getLogger(RunTimeResource.class.getName()).log(Level.SEVERE, "[getTask]", e);
            return ResponseEntity.status(HttpStatus.resolve(500)).body(e.getMessage());
        }
    }

    //TODO Create a pagination
    @RequestMapping(value = "/getCandidateTask", method = RequestMethod.GET)
    public ResponseEntity getCandidateTask(@RequestParam(name = "page", required = true, defaultValue = "0") int page,
            @RequestParam(name = "size", required = true, defaultValue = "10") int size, UsernamePasswordAuthenticationToken principal) {
        try {
            PageResponse<TaskResponse> response = this.service.getCandidateTasks((UserActiviti)principal.getPrincipal(), page, size);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Logger.getLogger(RunTimeResource.class.getName()).log(Level.SEVERE, "[getCandidateTask]", e);
            return ResponseEntity.status(HttpStatus.resolve(500)).body(e.getMessage());
        }
    }

    @RequestMapping(value = "/getMyTask", method = RequestMethod.GET)
    public ResponseEntity getMyTask(@RequestParam(name = "page", required = true, defaultValue = "0") int page,
            @RequestParam(name = "size", required = true, defaultValue = "10") int size,
            Principal principal) {
        try {
            PageResponse<TaskResponse> response = this.service.getMyTasks(principal.getName(), page, size);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Logger.getLogger(RunTimeResource.class.getName()).log(Level.SEVERE, "[getMyTask]", e);
            return ResponseEntity.status(HttpStatus.resolve(500)).body(e.getMessage());
        }
    }
    
    @RequestMapping(value = "/assigneeTask", method = RequestMethod.POST)
    public ResponseEntity assigneeTask(@RequestParam(name = "taskId")String taskId, Principal principal){
        try {
            this.service.assigneeTask(taskId, principal.getName());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            Logger.getLogger(RunTimeResource.class.getName()).log(Level.SEVERE, "[assigneeTask]", e);
            return ResponseEntity.status(HttpStatus.resolve(500)).body(e.getMessage());
        }
        
    }

    @RequestMapping(value = "/getInvolvedTasks", method = RequestMethod.GET)
    public ResponseEntity getInvolvedTasks(@RequestParam(name = "page", required = true, defaultValue = "0") int page,
            @RequestParam(name = "size", required = true, defaultValue = "10") int size, Principal principal) {
        try {
            PageResponse<TaskResponse> response = this.service.getInvolvedTasks(principal.getName(), page, size);
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

    @RequestMapping(value = "/comment", method = RequestMethod.POST)
    public ResponseEntity addComment(@RequestBody AddComment request, Principal principal) {
        try {

            Comment response = service.addComment(request, principal.getName());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Logger.getLogger(RunTimeResource.class.getName()).log(Level.SEVERE, "[addComment]", e);
            return ResponseEntity.status(HttpStatus.resolve(500)).body(e.getMessage());
        }
    }

    @RequestMapping(value = "/comment/{id}", method = RequestMethod.DELETE)
    public ResponseEntity deleteComment(@PathVariable(name = "id") String id) {
        try {

            service.deleteComment(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            Logger.getLogger(RunTimeResource.class.getName()).log(Level.SEVERE, "[deleteComment]", e);
            return ResponseEntity.status(HttpStatus.resolve(500)).body(e.getMessage());
        }
    }

    @RequestMapping(value = "/detail/{id}", method = RequestMethod.GET)
    public ResponseEntity detail(@PathVariable(name = "id") String id) {
        try {

            ProcessDetailResponse response = service.getDetail(id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Logger.getLogger(RunTimeResource.class.getName()).log(Level.SEVERE, "[detail]", e);
            return ResponseEntity.status(HttpStatus.resolve(500)).body(e.getMessage());
        }
    }

    @RequestMapping(value = "/attachment", method = RequestMethod.POST)
    public ResponseEntity createAttachment(@RequestPart(value = "file") MultipartFile file,
            @RequestPart(value = "processInstance") String processInstance) {
        try {
            AttachmentResponse response = this.service.createAttachament(processInstance, file);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Logger.getLogger(RepositoryResource.class.getName()).log(Level.SEVERE, "[post]", e);
            return ResponseEntity.status(HttpStatus.resolve(500)).body(e.getMessage());
        }
    }

    @RequestMapping(value = "/attachment/{attachmentId}", method = RequestMethod.DELETE)
    public ResponseEntity deleteAttachment(@PathVariable(value = "attachmentId") String attachmentId) {
        try {
            this.service.deleteAttachment(attachmentId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            Logger.getLogger(RepositoryResource.class.getName()).log(Level.SEVERE, "[post]", e);
            return ResponseEntity.status(HttpStatus.resolve(500)).body(e.getMessage());
        }
    }

    @RequestMapping(value = "/attachment", method = RequestMethod.GET)
    public ResponseEntity downloadAttachment(@RequestParam(value = "attachamentId") String attachamentId,
            HttpServletResponse response) {
        try {

            Attachment at = service.getAttachament(attachamentId);
            InputStream attachment = service.getAttachamentContent(attachamentId);

            org.apache.commons.io.IOUtils.copy(attachment, response.getOutputStream());
            response.flushBuffer();

            response.setContentType(at.getType());
            response.setHeader("Content-disposition", "attachment; filename=" + at.getName());

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            Logger.getLogger(RepositoryResource.class.getName()).log(Level.SEVERE, "[post]", e);
            return ResponseEntity.status(HttpStatus.resolve(500)).body(e.getMessage());
        }
    }

    @RequestMapping(value = "/process/{id}", method = RequestMethod.DELETE)
    public ResponseEntity deleteProcess(@RequestParam(name = "[]id") String[] id) {
        try {
            service.deleteProcess(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            Logger.getLogger(RepositoryResource.class.getName()).log(Level.SEVERE, "[post]", e);
            return ResponseEntity.status(HttpStatus.resolve(500)).body(e.getMessage());
        }
    }

}
