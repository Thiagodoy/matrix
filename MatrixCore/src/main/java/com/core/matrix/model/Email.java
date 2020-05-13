/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.model;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author thiag
 */
@Entity
@Table(name = "mtx_notificacao")
@Data
public class Email implements Model<Email>{
   
    
   public enum EmailStatus{
       QUEUE,
       READY,
       SENT,
       ERROR;
   }
   
   @Id
   @Column(name = "id_notificacao")
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   protected Long id;
    
   @Enumerated(EnumType.STRING)
   @Column(name = "status")
   protected EmailStatus status;
   
   @Column(name = "id_erro")
   protected Long error;
   
   @Column(name = "tentativas")
   protected Long retry;
   
   @Column(name = "dados")
   protected String data;
   
   @ManyToOne
   @JoinColumn(name = "id_template")
   protected Template template;
   
   
   @Column(name = "data_criacao")
   protected LocalDateTime createdAt;
   
   
   @PrePersist
   public void generateDate(){
       this.createdAt = LocalDateTime.now();
   }
   
   public String generateUiid(){
       String value = MessageFormat.format("{0}-{1}-{2}-{3}", status,data,createdAt,template);
       return  UUID.fromString(value).toString();
   }

}
