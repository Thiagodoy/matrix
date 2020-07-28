/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.model;

import com.core.matrix.dto.ContractPointDTO;
import com.core.matrix.exceptions.ContractNotAssociatedWithPointException;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Optional;
import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedNativeQuery;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Table;
import javax.persistence.Transient;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *
 * @author thiag
 */
@SqlResultSetMapping(name = "contractPointResult",
        classes = @ConstructorResult(targetClass = ContractPointDTO.class, columns = {
    @ColumnResult(name = "wbc_contrato", type = Long.class)
    ,
    @ColumnResult(name = "wbc_ponto_de_medicao", type = String.class)
}))
@NamedNativeQuery(query = "SELECT \n"
        + "    b.wbc_ponto_de_medicao, a.wbc_contrato\n"
        + "FROM\n"
        + "    mtx_contrato a\n"
        + "        INNER JOIN\n"
        + "    mtx_ponto_contrato c ON a.id_contrato = c.id_contrato\n"
        + "        INNER JOIN\n"
        + "    mtx_ponto_de_medicao b ON c.id_ponto_de_medicao = b.id_ponto_de_medicao\n"
        + "    where a.wbc_contrato in :contracts", name = "ContractMtx.associations", resultSetMapping = "contractPointResult")

@Entity
@Table(name = "mtx_contrato")
@Data
@EqualsAndHashCode(of = "wbcContract")
@JsonIgnoreProperties(value = {"point"})
public class ContractMtx implements Model<ContractMtx>, Serializable {

    private static final long serialVersionUID = -5336086836317164272L;

    @Id
    @Column(name = "id_contrato")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Column(name = "wbc_contrato")
    protected Long wbcContract;

    @Column(name = "wbc_codigo_contrato")
    protected Long codeWbcContract;

    @Column(name = "wbc_rateio")
    protected boolean isApportionment;

    @Column(name = "wbc_codigo_contrato_rateio")
    protected Long codeContractApportionment;

    @Column(name = "percentual_de_perda")
    protected Double percentOfLoss;

    @Column(name = "fator_atendimento_carga")
    protected Double factorAttendanceCharge;

    @Column(name = "wbc_submercado")
    protected Integer wbcSubmercado;

    @JsonIgnore
    @Column(name = "data_criacao")
    protected LocalDateTime createdAt;

    @JsonIgnore
    @Column(name = "data_ultima_alteracao")
    protected LocalDateTime lastUpdate;

    @Column(name = "unidade_consumidora")
    protected boolean isConsumerUnit;

    @Column(name = "flat")
    protected boolean isFlat;

    @Column(name = "nome_empresa")
    protected String nameCompany;

    @Column(name = "nome_fantasia")
    protected String nickname;

    @Column(name = "cnpj")
    protected String cnpj;

    @Column(name = "apelido")
    protected String nickname1;
    
    @Transient
    protected  String pointAssociated;       
    
    public String getPoint()throws ContractNotAssociatedWithPointException{
        
        if(Optional.ofNullable(this.pointAssociated).isPresent()){
            return this.pointAssociated;
        }else{
            throw new ContractNotAssociatedWithPointException();
        }
    }

    @PrePersist
    protected void generateDate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void generateUpdateDate() {
        this.lastUpdate = LocalDateTime.now();
    }
}
