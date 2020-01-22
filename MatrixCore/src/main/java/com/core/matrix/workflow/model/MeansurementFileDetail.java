/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.workflow.model;

import com.core.matrix.dto.FileDetailDTO;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
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
@Table(name = "mtx_arquivo_de_medicao_detalhe")
@Data
public class MeansurementFileDetail {

    @Id
    @Column(name = "id_arquivo_de_medicao_detalhe")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_arquivo_de_medicao")
    private Long idMeansurementFile;

    @Column(name = "ponto_medicao")
    private String meansurementPoint;

    @Column(name = "data")
    private LocalDateTime date;

    @Column(name = "hora")
    private Long hour;

    @Column(name = "tipo_energia")
    private String energyType;

    @Column(name = "ativa_geracao")
    private Double generationActive;

    @Column(name = "ativa_consumo")
    private Double consumptionActive;

    @Column(name = "reativa_geracao")
    private Double generationReactivate;

    @Column(name = "reativa_consumo")
    private Double consumptionReactivate;
    
    
     
    public MeansurementFileDetail(FileDetailDTO detail){
        
        
        
    }
}
