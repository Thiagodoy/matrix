/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author thiag
 */
@Entity
@Table(name = "mtx_contrato_proinfa")
@IdClass(value = ContractProInfa.IdClass.class)
@Data
public class ContractProInfa {

    @Id
    @Column(name = "wbc_contrato")
    private Long wbcContract;

    @Id
    @Column(name = "wbc_ponto_de_medicao")
    private String meansurementPoint;

    @Id
    @Column(name = "ano")
    private Long year;

    @Id
    @Column(name = "mes")
    private Long month;

    @Column(name = "proinfa")
    private Double proinfa;

    @Data
    public static class IdClass implements Serializable {

        private Long wbcContract;
        private String meansurementPoint;
        private Long year;
        private Long month;
    }

}
