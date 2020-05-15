/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.model;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author thiag
 */
@Entity
@Table(name = "mtx_notificacao")
@Data
public class Notification {
    
    
    public enum NotificationType{
        USER_TASK_PENDING,
        GROUP_TASK_PENDING
    }
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_notificacao")
    private Long id;
    
    @Column(name = "usuario")
    private String to;
    
    @Column(name = "id_tarefa")
    private String taskId;
    
    @Column(name = "nome_tarefa")
    private String nameTask;
    
    @Column(name = "id_processo")
    private String processId;  
    
    @Column(name = "nome_processo")
    private String nameProcessId;  
    
    @Column(name = "visualizado")
    private boolean isRead;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo")
    private NotificationType type;
    
    @Column(name = "data_criacao")
    private LocalDateTime createdAt;
    
    public  Notification(){
        this.createdAt = LocalDateTime.now();
    }
    
    
}
