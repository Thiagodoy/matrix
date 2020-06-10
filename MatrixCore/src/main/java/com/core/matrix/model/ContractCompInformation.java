/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.model;

import com.core.matrix.dto.ContractInformationDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedNativeQuery;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author thiag
 */
@SqlResultSetMapping(name = "contractInfoDTO",
        classes = @ConstructorResult(
                targetClass = ContractInformationDTO.class,
                columns = {
                    @ColumnResult(name = "wbc_contrato", type = Long.class)
                    ,
                        @ColumnResult(name = "wbc_ponto_de_medicao", type = String.class)
                    ,
                        @ColumnResult(name = "percentual_de_perda", type = Double.class)
                    ,
                        @ColumnResult(name = "fator_atendimento_carga", type = Double.class)
                    ,
                        @ColumnResult(name = "wbc_submercado", type = Integer.class)
                    ,
                        @ColumnResult(name = "proinfa", type = Double.class),}))

@NamedNativeQuery(name = "ContractCompInformation.listByPoint", query = "SELECT \n"
        + "    a.wbc_contrato,\n"
        + "    a.wbc_ponto_de_medicao,\n"
        + "    b.percentual_de_perda,\n"
        + "    b.proinfa,\n"
        + "    b.fator_atendimento_carga,\n"
        + "    b.wbc_submercado\n"
        + "FROM\n"
        + "    matrix.mtx_ponto_de_medicao a\n"
        + "        INNER JOIN\n"
        + "    matrix.mtx_contrato_informacao_complementar b ON a.wbc_contrato = b.wbc_contrato \n"
        + " where a.wbc_ponto_de_medicao = :point",
        resultSetMapping = "contractInfoDTO")

@Entity
@Table(name = "mtx_contrato_informacao_complementar")
@Data
@JsonIgnoreProperties(value = {"lastUpdate", "createAt"})
public class ContractCompInformation  implements Serializable,Model<ContractCompInformation>{

    protected static final long serialVersionUID = -6849689118029240029L;
    
    @Id
    @Column(name = "wbc_contrato")
    protected Long wbcContract;

    @Column(name = "wbc_ponto_de_medicao")
    protected String meansurementPoint;

    @Column(name = "wbc_codigo_contrato")
    protected Long codeWbcContract;

    @Column(name = "wbc_rateio")
    protected Long isApportionment;

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

    @OneToMany(fetch = FetchType.EAGER, orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "wbc_contrato")
    protected List<ContractProInfa> proinfas;    
    
    @Column(name = "unidade_consumidora")
    protected String isConsumerUnit;

    @PrePersist
    public void generateCreatedAt() {
        this.createdAt = LocalDateTime.now();
        
        if(!Optional.ofNullable(this.codeContractApportionment).isPresent()){
            this.codeContractApportionment = 0L;
        }
        
    }

    @PreUpdate
    public void generateLastUpdate() {
        this.lastUpdate = LocalDateTime.now();
    }
    
    public Long getId() {
        return this.wbcContract;
    }

}
