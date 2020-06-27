/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author thiag
 */
@Entity
@Table(name = "mtx_arquivo_de_medicao_alcada")
@Data
public class MeansurementFileAuthority implements Serializable, Model<MeansurementFileAuthority> {

    protected static final long serialVersionUID = 4364052964932105596L;
    
    @Id
    @Column(name = "id_arquivo_de_medicao_alcada")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Column(name = "id_arquivo_de_medicao")
    protected Long idMeansurementFile;

    @Column(name = "alcada")
    protected String authority;

    @Column(name = "resultado")
    protected String result;

    @Column(name = "justificativa")
    protected String justify;

    @Column(name = "act_id_usuario_aprovador")
    protected String user;

    @Column(name = "act_nome_usuario_aprovador")
    protected String userName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING)
    @Column(name = "data")
    protected LocalDateTime createdAt;
    
    @Column(name = "act_id_processo")
    protected String processInstanceId;
    
    @Column(name = "valor")
    protected Double oldValue;
    
    @Column(name = "valor_corrigido")
    protected Double newValue;
    
    @PrePersist
    public void generateCreateAt(){
        this.createdAt = LocalDateTime.now();
    }

}
