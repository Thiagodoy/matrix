/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.workflow.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author thiag
 */
@Entity
@Table(name = "act_hi_comment")
@Data
public class CommentActiviti {
    
    @Id
    @Column(name = "ID_")
    private String id;
    
    @Column(name =  "USER_ID_")
    private String userID;
    
    @Column(name = "Message_")
    private String message;
}
