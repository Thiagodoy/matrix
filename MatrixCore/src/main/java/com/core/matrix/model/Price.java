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
@Table(name = "mtx_preco_base")
@Data
public class Price implements Serializable {

    private static final long serialVersionUID = 6246297181135230555L;

    @Id
    @Column(name = "id_preco_base")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "wbc_submercado")
    private Long subMarket;

    @Column(name = "wbc_descricao_submercado")
    private String description;

    @Column(name = "pld")
    private Double pld;

    public void update(Price price) {

        if (Optional.ofNullable(price.getSubMarket()).isPresent() && !this.subMarket.equals(price.getSubMarket())) {
            this.subMarket = price.getSubMarket();
        }

        if (Optional.ofNullable(price.getDescription()).isPresent() && !this.description.equals(price.getDescription())) {
            this.description = price.getDescription();
        }

        if (Optional.ofNullable(price.getPld()).isPresent() && !this.pld.equals(price.getPld())) {
            this.pld = price.getPld();
        }

    }

}
