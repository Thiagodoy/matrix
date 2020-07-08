/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import java.time.LocalDateTime;
import java.util.Optional;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 *
 * @author thiag
 */
@Entity
@Table(name = "mtx_template")
@Data
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class Template implements Model<Template>{
    
    
    
    public enum TemplateBusiness{
        FORGOT_PASSWORD,
        USER_TASK_PENDING,
        WELCOME_USER,
        PROCESS_ERROR,
        PROCESS_BILLING_ERROR,
        FINISHED_UPLOAD_LOTE_FILE,
        GROUP_TASK_PENDING,
        BILLING_WHITOUT_REPURCHASE,
        BILLING_WITH_REPURCHASE;
    }
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_template")
    protected Long id;
    
    @Column(name = "template",columnDefinition = "longtext")
    protected String template;
    
    @Column(name = "assunto")
    protected String subject;    
    
    @Enumerated(EnumType.STRING)
    @Column(name = "logica")
    protected TemplateBusiness business;
    
    @Version
    protected Long version;
    
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @Column(name = "data_criacao")
    protected LocalDateTime createdAt;
    
    @Column(name = "parametros")
    protected String attachments;
    
    @PrePersist
    public void generatedDate(){
        this.createdAt = LocalDateTime.now();        
        
        if(!Optional.ofNullable(this.attachments).isPresent()){
            this.attachments = "";
        }
    }
    
    
}
