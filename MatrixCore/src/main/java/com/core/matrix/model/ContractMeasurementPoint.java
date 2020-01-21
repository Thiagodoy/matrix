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
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author thiag
 */
@Entity
@Table(name = "mtx_ponto_de_medicao")
@Data
public class ContractMeasurementPoint {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "")
    private Long id;
    
    @Column(name = "wbc_contrato")
    private Long wbcContract;
    
    @Column(name = "wbc_ponto_de_medicao")
    private String point;
    
     @Column(name = "data_criacao")
    private LocalDateTime createdAt;    
    
    @Column(name = "data_ultima_alteracao")
    private LocalDateTime lastUpdate;    
    
    
    @PrePersist
    public void generateCreatedAt(){
        this.createdAt = LocalDateTime.now();
    }
    
    
    @PreUpdate
    public void generateLastUpdate(){
        this.lastUpdate = LocalDateTime.now();
    }
    
}
