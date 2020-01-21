/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.model;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
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
@Table(name = "mtx_contrato_informacao_complementar")
@Data
public class ContractCompInformation {
    
    @Id    
    @Column(name = "wbc_contrato")
    private Long wbcContract;
    
    @Column(name = "percentual_de_perda")
    private Double percentOfLoss;
    
    @Column(name = "fator_atendimento_carga")
    private Double factorAttendanceCharge;
    
    @Column(name = "proinfa")
    private Double proinfa;
    
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
