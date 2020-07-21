/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.model;

import com.core.matrix.request.ManagerRequest;
import static com.core.matrix.utils.Constants.TABLE_SEQUENCES;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
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

    @Column(name = "cnpj")
    private String cnpj;

    @Column(name = "razao_social")
    private String companyName;

    @Column(name = "nome_fantasia")
    private String fancyName;

    @Column(name = "apelido")
    private String nickName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING)
    @Column(name = "data_criacao")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING)
    @Column(name = "data_ultima_alteracao")
    private LocalDateTime lastUpdate;

    @OneToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    @JoinColumn(name = "id_gestor")
    private List<ContactManager> contacts;

    public Manager() {
    }

    public Manager(ManagerRequest request) {
        this.companyName = request.getCompanyName();
        this.fancyName = request.getFancyName();
        this.nickName = request.getNickName();
        this.cnpj = request.getCnpj();
        this.contacts = request.getContacts().stream().map(cr -> new ContactManager(cr)).collect(Collectors.toList());

    }

    @PrePersist
    public void generateDateCreated() {
        this.createdAt = LocalDateTime.now();

    }

    @PreUpdate
    public void generateLastUpdate() {
        this.lastUpdate = LocalDateTime.now();
    }

}
