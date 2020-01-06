/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.model;

import java.time.LocalDateTime;
import java.util.List;
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
@Table(name = "mtx_gestor")
@Data
public class Manager {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_gestor")
    private Long id;
    
    @Column(name = "razao_social")
    private String companyName;
    
    @Column(name = "nome_fantasia")
    private String fancyName;
    
    @Column(name = "apelido")
    private String nickName;
    
    @Column(name = "data_criacao")
    private LocalDateTime createdAt;
    
    @Column(name = "data_ultima_alteracao")
    private LocalDateTime lastUpdate;
    
    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_gestor")
    private List<ContactManager> contacts;
    
}
