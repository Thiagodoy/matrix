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
import javax.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *
 * @author thiag
 */
@Entity
@Table(name = "mtx_aqruivo_de_medicao_recompra")
@Data
@EqualsAndHashCode
public class MeansurementRepurchase implements Serializable, Model<MeansurementRepurchase> {

    protected static final long serialVersionUID = -7514701442313318250L;

    @Id
    @Column(name = "id_aqruivo_de_medicao_recompra")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Column(name = "id_arquivo_de_medicao")
    protected Long meansurementFileId;

    @Column(name = "id_produtos")
    protected Long productId;

    @Column(name = "preco_contrato")
    protected Double priceContract;

    @Column(name = "notional_contratado")
    protected Double notionalHired;

    @Column(name = "take")
    protected Double take;

    @Column(name = "volume_recompra")
    protected Double repurchaseVolume;

    @Column(name = "preco_recompra")
    protected Double repurchasePrice;

    @Column(name = "notional_recompra")
    protected Double repurchaseNotional;

    @Column(name = "novo_faturamento")
    protected Double newBilling;

    @Column(name = "novo_preco")
    protected Double newPrice;

    @Column(name = "novo_notional")
    protected Double newNotional;

    @Column(name = "recompra")
    protected String repurchase;
    
    @Column(name = "act_id_processo")
    protected String processInstanceId;

    @Column(name = "pld")
    protected Double pld;
        
    @Column(name = "spread")
    protected Double spread; 
    
    @Column(name = "wbc_contrato")
    protected Long wbcContract;
    
}
