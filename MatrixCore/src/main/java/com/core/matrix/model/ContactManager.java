/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.model;

import com.core.matrix.request.ContactManagerRequest;
import static com.core.matrix.utils.Constants.TABLE_SEQUENCES;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author thiag
 */
@Entity
@Table(name = "mtx_contato_gestor")
@Data
public class ContactManager {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = TABLE_SEQUENCES)
    @Column(name = "id_contato_gestor")
    private Long id;

    
    
    @Column(name = "id_gestor",nullable = false,updatable = false)
    private Long manager;

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

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING)
    @Column(name = "data_criacao")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING)
    @Column(name = "data_ultima_alteracao")
    private LocalDateTime lastUpdate;
    
    
    
    public ContactManager(ContactManagerRequest request) {

        this.email = request.getEmail();
        this.name = request.getName();
        this.telephone1 = request.getTelephone1();
        this.telephone2 = request.getTelephone2();
        this.telephone3 = request.getTelephone3();
        this.typeContact = request.getTypeContact();  
        this.manager = request.getManager();
        
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
