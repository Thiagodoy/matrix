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
@Table(name = "E_TIPO_AGENTE")
@Data
public class AgentType {

    @Id
    @Column(name = "nCdTipoAgente")
    private Long nCdTipoAgente;
    
    @Column(name = "sDsTipoAgente")
    private String sDsTipoAgente;    
    
    @Column(name = "nCdTipoAgenteCCEE")
    private Long nCdTipoAgenteCCEE;

}
