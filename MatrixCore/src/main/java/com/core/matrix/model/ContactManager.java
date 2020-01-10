/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.model;

import com.core.matrix.request.ContactManagerRequest;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author thiag
 */
@Entity
@Table
@Data
public class ContactManager {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_contato_gestor")
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_gestor")
    private Manager manager;

    @Column(name = "nome")
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "telefone_1")
    private String telephone1;

    @Column(name = "telefone_2")
    private String telephone2;

    @Column(name = "telefone_3")
    private String telephone3;

    @Column(name = "tipo_contato")
    private String typeContact;

    @Column(name = "data_criacao")
    private LocalDateTime createdAt;

    @Column(name = "data_ultima_alteracao")
    private LocalDateTime lastUpdate;

    public ContactManager(ContactManagerRequest request) {

        this.email = request.getEmail();
        this.name = request.getName();
        this.telephone1 = request.getTelephone1();
        this.telephone2 = request.getTelephone2();
        this.telephone3 = request.getTelephone3();
        this.typeContact = request.getTypeContact();
    }

    public ContactManager() {

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
