/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author thiag
 */
@Entity
@Table(name = "mtx_log")
@Data
public class Log implements Model<Log>{

    
    public enum LogType{
        LAYOUT_INVALID,
        DATA_INVALID,
        DATA_NOT_PERSISTED        
    }
    
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_log")
    protected Long id;

    @Column(name = "anexo")
    protected String attachment;

    @Column(name = "id_arquivo")
    protected Long fileId;

    @Column(name = "nome_processo")
    protected String processName;

    @Column(name = "act_id_processo")
    protected String processInstanceId;

    @Column(name = "mensagem")
    protected String message;

    @Column(name = "mensagem_erro_aplicacao")
    protected String messageErrorApplication;
    
    @Column(name = "nome_atividade")
    protected String activitiName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING)
    @Column(name = "data_criacao")
    protected LocalDateTime createAt;
    
    @Column(name = "tipo")
    @Enumerated(EnumType.STRING)
    protected LogType type;

    @PrePersist
    public void generateDate() {
        this.createAt = LocalDateTime.now();
    }

}
