/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Optional;
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
public class MeansurementFileAuthority implements Serializable {

    @Id
    @Column(name = "id_arquivo_de_medicao_alcada")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_arquivo_de_medicao")
    private Long idMeansurementFile;

    @Column(name = "alcada")
    private String authority;

    @Column(name = "resultado")
    private String result;

    @Column(name = "justificativa")
    private String justify;

    @Column(name = "act_id_usuario_aprovador")
    private String user;

    @Column(name = "act_nome_usuario_aprovador")
    private String userName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING)
    @Column(name = "data")
    private LocalDateTime createdAt;
    
    @Column(name = "act_id_processo")
    private String processInstanceId;
    
    @PrePersist
    public void generateCreateAt(){
        this.createdAt = LocalDateTime.now();
    }

    public void update(MeansurementFileAuthority entiAuthority) {

        if (Optional.ofNullable(entiAuthority.getAuthority()).isPresent() && !this.authority.equals(entiAuthority.getAuthority())) {
            this.authority = entiAuthority.getAuthority();
        }

        if (Optional.ofNullable(entiAuthority.getResult()).isPresent() && !this.result.equals(entiAuthority.getResult())) {
            this.result = entiAuthority.getResult();
        }

        if (Optional.ofNullable(entiAuthority.getJustify()).isPresent() && !this.justify.equals(entiAuthority.getJustify())) {
            this.justify = entiAuthority.getJustify();
        }

        if (Optional.ofNullable(entiAuthority.getUser()).isPresent() && !this.user.equals(entiAuthority.getUser())) {
            this.user = entiAuthority.getUser();
        }

        if (Optional.ofNullable(entiAuthority.getUserName()).isPresent() && !this.userName.equals(entiAuthority.getUserName())) {
            this.userName = entiAuthority.getUserName();
        }

    }

}
