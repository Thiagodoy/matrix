/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.response;

import java.util.Date;
import java.util.List;
import lombok.Data;
import org.activiti.engine.task.Attachment;
import org.activiti.engine.task.Comment;

/**
 *
 * @author thiag
 */
@Data
public class ProcessDetailResponse {

    private List<Comment> comments;
    private List<AttachmentResponse> attachments;
    private Date processCreatedDate;
    private String processCreatedUser;    

}
