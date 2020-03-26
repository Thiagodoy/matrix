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
@Table(name = "mtx_alcada_aprovacao")
@Data
public class AuthorityApproval implements Serializable {

    @Id
    @Column(name = "id_alcada_aprovacao")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "alcada")
    private String authority;

    @Column(name = "faixa_min")
    private Double min;

    @Column(name = "faixa_max")
    private Double max;

    public void update(AuthorityApproval aprovation) {

        if (Optional.ofNullable(aprovation.getAuthority()).isPresent() && !this.authority.equals(aprovation.getAuthority())) {
            this.authority = aprovation.getAuthority();
        }

        if (Optional.ofNullable(aprovation.getMin()).isPresent() && !this.min.equals(aprovation.getMin())) {
            this.min = aprovation.getMin();
        }

        if (Optional.ofNullable(aprovation.getMax()).isPresent() && !this.max.equals(aprovation.getMax())) {
            this.max = aprovation.getMax();
        }

    }

}
