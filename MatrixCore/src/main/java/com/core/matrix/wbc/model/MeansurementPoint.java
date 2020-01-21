/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.wbc.model;

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
@Table(name = "CE_PONTO_MEDICAO")
@Data
public class MeansurementPoint {
    
    
    @Id
    @Column(name = "nCdPontoMedicao")
    private Long id;
    
    @Column(name = "nCdEmpresa")
    private Long company;
    
    @Column(name = "sDsPontoMedicao")
    private String descriptionPoint;
    
    @Column(name = "sCdImportacaoPontoMedicao")
    private String code;
    
    @Column(name = "nIdTipoPonto")
    private Long type;    
    
}
