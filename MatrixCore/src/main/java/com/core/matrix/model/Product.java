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
import javax.persistence.PrePersist;
import javax.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

/**
 *
 * @author thiag
 */
@Entity
@Table(name = "mtx_produtos")
@Data
public class Product implements Serializable {

    @Id
    @Column(name = "id_produtos")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "wbc_submercado")
    private Long subMarket;

    @Column(name = "produto")
    private String name;

    @Column(name = "pld")
    private Double pld;

    @Column(name = "spread_compra")
    private Double spreadPurchase;

    @Column(name = "spread_venda")
    private Double spreadSale;

    @Generated(GenerationTime.ALWAYS)
    @Column(name = "preco_compra")
    private Double pricePurchase;

    @Generated(GenerationTime.ALWAYS)
    @Column(name = "preco_venda")
    private Double priceSale;      
    

    public void update(Product product) {

        if (Optional.ofNullable(product.getSubMarket()).isPresent() && !this.subMarket.equals(product.getSubMarket())) {
            this.subMarket = product.getSubMarket();
        }

        if (Optional.ofNullable(product.getName()).isPresent() && !this.name.equals(product.getName())) {
            this.name = product.getName();
        }

        if (Optional.ofNullable(product.getPld()).isPresent() && !this.pld.equals(product.getPld())) {
            this.pld = product.getPld();
        }

        if (Optional.ofNullable(product.getSpreadPurchase()).isPresent() && !this.spreadPurchase.equals(product.getSpreadPurchase())) {
            this.spreadPurchase = product.getSpreadPurchase();
        }

        if (Optional.ofNullable(product.getSpreadSale()).isPresent() && !this.spreadSale.equals(product.getSpreadSale())) {
            this.spreadSale = product.getSpreadSale();
        }

//        if (Optional.of(product.getPricePurchase()).isPresent() && !this.pricePurchase.equals(product.getPricePurchase())) {
//            this.pricePurchase = product.getPricePurchase();
//        }
//
//        if (Optional.of(product.getPriceSale()).isPresent() && !this.priceSale.equals(product.getPriceSale())) {
//            this.priceSale = product.getPriceSale();
//        }

    }

}
