/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.model;


import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author thiag
 */
@Entity
@Table(name = "mtx_ponto_de_medicao")
@Data
public class MeansurementPointMtx implements Model<MeansurementPointMtx>, Serializable {

    private static final long serialVersionUID = -1109080518946804534L;

    @Id
    @Column(name = "id_ponto_de_medicao")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "wbc_ponto_de_medicao", unique = true)
    private String point;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING)
    @Column(name = "data_criacao")
    private LocalDateTime createAt;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "wbc_ponto_de_medicao", referencedColumnName ="wbc_ponto_de_medicao" )
    private List<MeansurementPointProInfa> proinfas;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "mtx_ponto_contrato",
            joinColumns = {
                @JoinColumn(name = "id_ponto_de_medicao")},
            inverseJoinColumns = {
                @JoinColumn(name = "id_contrato")})
    private Set<ContractMtx> contracts;

}
