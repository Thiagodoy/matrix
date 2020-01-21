/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
import lombok.Data;
import org.activiti.engine.task.Attachment;

/**
 *
 * @author thiag
 */
@Data
public class AttachmentResponse{

    
    private String id; 
    private String name;
    private String description;
    private String type;
    private String taskId;
    private String processInstanceId;
    private String url;
    private String userId;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING)
    private Date time;
    
    
    public AttachmentResponse(Attachment attachment){
        
        this.id = attachment.getId();
        this.name = attachment.getName();
        this.description = attachment.getDescription();
        this.type = attachment.getType();
        this.taskId = attachment.getTaskId();
        this.processInstanceId = attachment.getProcessInstanceId();
        this.url = attachment.getUrl();
        this.userId = attachment.getUserId();
        this.time = attachment.getTime();
        
    }
    
}
