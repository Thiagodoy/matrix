/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.model;

import static com.core.matrix.utils.Constants.TABLE_SEQUENCES;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *
 * @author thiag
 */
@Entity
@Table(name = "mtx_contrato_proinfa")
@Data
@EqualsAndHashCode
public class ContractProInfa implements Serializable, Model<ContractProInfa>{

    protected static final long serialVersionUID = -4258355186535810797L;    
    
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = TABLE_SEQUENCES)
    @Column(name = "id_proinfa")
    protected Long id;
    
    
    @Column(name = "wbc_contrato", nullable = false)
    protected Long wbcContract;

    
    @Column(name = "wbc_ponto_de_medicao")
    protected String meansurementPoint;

    
    @Column(name = "ano")
    protected Long year;

    
    @Column(name = "mes")
    protected Long month;

    @Column(name = "proinfa")
    protected Double proinfa;   

}
