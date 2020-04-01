/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.model;

import java.io.Serializable;
import java.util.Optional;
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
@Table(name = "mtx_aqruivo_de_medicao_recompra")
@Data
public class MeansurementRepurchase implements Serializable {

    @Id
    @Column(name = "id_aqruivo_de_medicao_recompra")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_arquivo_de_medicao")
    private Long meansurementFileId;

    @Column(name = "id_produtos")
    private Long productId;

    @Column(name = "preco_contrato")
    private Double priceContract;

    @Column(name = "notional_contratado")
    private Double notionalHired;

    @Column(name = "take")
    private Double take;

    @Column(name = "volume_recompra")
    private Double repurchaseVolume;

    @Column(name = "preco_recompra")
    private Double repurchasePrice;

    @Column(name = "notional_recompra")
    private Double repurchaseNotional;

    @Column(name = "novo_faturamento")
    private Double newBilling;

    @Column(name = "novo_preco")
    private Double newPrice;

    @Column(name = "novo_notional")
    private Double newNotional;

    @Column(name = "recompra")
    private String repurchase;
    
    @Column(name = "act_id_processo")
    private String processInstanceId;

    @Column(name = "pld")
    private Double pld;
        
    @Column(name = "spread")
    private Double spread;
            
    public void update(MeansurementRepurchase meansurementRepurchase) {

        if (Optional.ofNullable(meansurementRepurchase.getMeansurementFileId()).isPresent() && !this.meansurementFileId.equals(meansurementRepurchase.getMeansurementFileId())) {
            this.meansurementFileId = meansurementRepurchase.getMeansurementFileId();
        }

        if (Optional.ofNullable(meansurementRepurchase.getProductId()).isPresent() && !this.productId.equals(meansurementRepurchase.getProductId())) {
            this.productId = meansurementRepurchase.getProductId();
        }

        if (Optional.ofNullable(meansurementRepurchase.getPriceContract()).isPresent() && !this.priceContract.equals(meansurementRepurchase.getPriceContract())) {
            this.priceContract = meansurementRepurchase.getPriceContract();
        }

        if (Optional.ofNullable(meansurementRepurchase.getNotionalHired()).isPresent() && !this.notionalHired.equals(meansurementRepurchase.getNotionalHired())) {
            this.notionalHired = meansurementRepurchase.getNotionalHired();
        }

        if (Optional.ofNullable(meansurementRepurchase.getTake()).isPresent() && !this.take.equals(meansurementRepurchase.getTake())) {
            this.take = meansurementRepurchase.getTake();
        }

        if (Optional.ofNullable(meansurementRepurchase.getRepurchaseVolume()).isPresent() && !this.repurchaseVolume.equals(meansurementRepurchase.getRepurchaseVolume())) {
            this.repurchaseVolume = meansurementRepurchase.getRepurchaseVolume();
        }

        if (Optional.ofNullable(meansurementRepurchase.getRepurchasePrice()).isPresent() && !this.repurchasePrice.equals(meansurementRepurchase.getRepurchasePrice())) {
            this.repurchasePrice = meansurementRepurchase.getRepurchasePrice();
        }

        if (Optional.ofNullable(meansurementRepurchase.getRepurchaseNotional()).isPresent() && !this.repurchaseNotional.equals(meansurementRepurchase.getRepurchaseNotional())) {
            this.repurchaseNotional = meansurementRepurchase.getRepurchaseNotional();
        }

        if (Optional.ofNullable(meansurementRepurchase.getNewBilling()).isPresent() && !this.newBilling.equals(meansurementRepurchase.getNewBilling())) {
            this.newBilling = meansurementRepurchase.getNewBilling();
        }

        if (Optional.ofNullable(meansurementRepurchase.getNewPrice()).isPresent() && !this.newPrice.equals(meansurementRepurchase.getNewPrice())) {
            this.newPrice = meansurementRepurchase.getNewPrice();
        }

        if (Optional.ofNullable(meansurementRepurchase.getNewNotional()).isPresent() && !this.newNotional.equals(meansurementRepurchase.getNewNotional())) {
            this.newNotional = meansurementRepurchase.getNewNotional();
        }

        if (Optional.ofNullable(meansurementRepurchase.getRepurchase()).isPresent() && !this.repurchase.equals(meansurementRepurchase.getRepurchase())) {
            this.repurchase = meansurementRepurchase.getRepurchase();
        }
        
         if (Optional.ofNullable(meansurementRepurchase.getProcessInstanceId()).isPresent() && !this.processInstanceId.equals(meansurementRepurchase.getProcessInstanceId())) {
            this.processInstanceId = meansurementRepurchase.getProcessInstanceId();
        }
         
        if (Optional.ofNullable(meansurementRepurchase.getPld()).isPresent() && !this.pld.equals(meansurementRepurchase.getPld())) {
            this.pld = meansurementRepurchase.getPld();
        }
                
        if (Optional.ofNullable(meansurementRepurchase.getSpread()).isPresent() && !this.spread.equals(meansurementRepurchase.getSpread())) {
            this.spread = meansurementRepurchase.getSpread();
        }
    }

}
