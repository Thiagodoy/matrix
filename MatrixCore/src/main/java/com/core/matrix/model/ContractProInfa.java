/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author thiag
 */
@Entity
@Table(name = "mtx_contrato_proinfa")
@Data
public class ContractProInfa {

    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_proinfa")
    private Long id;
    
    
    @Column(name = "wbc_contrato", nullable = false)
    private Long wbcContract;

    
    @Column(name = "wbc_ponto_de_medicao")
    private String meansurementPoint;

    
    @Column(name = "ano")
    private Long year;

    
    @Column(name = "mes")
    private Long month;

    @Column(name = "proinfa")
    private Double proinfa;

   

}