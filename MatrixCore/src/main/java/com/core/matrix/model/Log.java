/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.model;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
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
public class Log {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_log")
    private Long id;
    
    @Column(name = "anexo")
    private String attachment;    
    
    @Column(name = "id_arquivo")
    private Long fileId;  
    
    @Column(name = "nome_processo")
    private String nameProcesso;  
    
    @Column(name = "mensagem")
    private String message;
    
    @Column(name = "data_criacao")
    private LocalDateTime createAt;
    
    
    @PrePersist
    public void generateDate(){
        this.createAt = LocalDateTime.now();
    }
    
    
    
}
