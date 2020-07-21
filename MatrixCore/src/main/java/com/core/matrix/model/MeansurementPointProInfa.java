/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.model;

import static com.core.matrix.utils.Constants.TABLE_SEQUENCES;
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
@Table(name = "mtx_ponto_de_medicao_pro_infa")
@Data
public class MeansurementPointProInfa implements Model<MeansurementPointProInfa>{
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;
    
    @Column(name = "wbc_ponto_de_medicao", nullable = false)
    protected String point;
    
    @Column(name = "ano")
    protected Long year;
    
    @Column(name = "mes")
    protected Long month;

    @Column(name = "proinfa")
    protected Double proinfa;  
    
    
}
