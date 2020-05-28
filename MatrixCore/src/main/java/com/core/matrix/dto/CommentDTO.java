/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.dto;

import java.util.Date;
import lombok.Data;
import org.activiti.engine.impl.persistence.entity.CommentEntity;
import org.activiti.engine.task.Comment;

/**
 *
 * @author thiag
 */
@Data
public class CommentDTO {

    private String id;
    private String type;
    private String userId;
    private Date time;
    private String taskId;
    private String processInstanceId;

    private String fullMessage;
    private String photo;
    private String username;

    public CommentDTO(Comment comment) {

        this.id = comment.getId();
        this.type = comment.getType();
        this.userId = comment.getUserId();
        this.time = comment.getTime();
        this.taskId = comment.getTaskId();
        this.processInstanceId = comment.getProcessInstanceId();
        this.fullMessage = comment.getFullMessage();
    }

}
