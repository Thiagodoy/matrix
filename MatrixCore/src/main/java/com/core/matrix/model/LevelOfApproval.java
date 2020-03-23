/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author aloysio
 */
@Entity
@Table(name = "mtx_alcada_aprovacao")
@Data
public class LevelOfApproval {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_alcada_aprovacao")
    private Long id;

    @Column(name = "alcada")
    private String level;

    @Column(name = "faixa_min")
    private Long minimumRange;

    @Column(name = "faixa_max")
    private Long maximumRange;

}
