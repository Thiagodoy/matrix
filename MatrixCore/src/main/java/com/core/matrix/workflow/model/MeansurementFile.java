/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.workflow.model;

import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author thiag
 */
@Entity
@Table(name = "mtx_arquivo_de_medicao")
@Data
public class MeansurementFile {

    @Id
    @Column(name = "id_arquivo_de_medicao")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "mes")
    private Long month;

    @Column(name = "ano")
    private Long year;

    @Column(name = "arquivo")
    private Long file;

    @Column(name = "status")
    private String status;

    @Column(name = "act_id_usuario")
    private String user;

    @Column(name = "data_criação")
    private LocalDateTime createdAt;

    @Column(name = "data_ultima_alteracao")
    private LocalDateTime updatedAt;
    
    
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "id_arquivo_de_medicao")
    private List<MeansurementFileDetail>details;
    

}
