/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.model;

import com.core.matrix.dto.ContractInformationDTO;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedNativeQuery;
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
                        @ColumnResult(name = "wbc_contrato", type = Long.class),
                        @ColumnResult(name = "wbc_ponto_de_medicao", type = String.class),
                        @ColumnResult(name = "percentual_de_perda", type = Double.class),
                        @ColumnResult(name = "proinfa", type = Double.class),
                        @ColumnResult(name = "fator_atendimento_carga", type = Double.class),                                   
                    
                }))


@NamedNativeQuery(name = "ContractCompInformation.listByPoint",query = "SELECT \n" +
"    a.wbc_contrato,\n" +
"    a.wbc_ponto_de_medicao,\n" +
"    b.percentual_de_perda,\n" +
"    b.proinfa,\n" +
"    b.fator_atendimento_carga\n" +
"FROM\n" +
"    matrix.mtx_ponto_de_medicao a\n" +
"        INNER JOIN\n" +
"    matrix.mtx_contrato_informacao_complementar b ON a.wbc_contrato = b.wbc_contrato \n"
        + " where a.wbc_ponto_de_medicao = :point",
        resultSetMapping = "contractInfoDTO")



@Entity
@Table(name = "mtx_contrato_informacao_complementar")
@Data
public class ContractCompInformation {
    
    @Id    
    @Column(name = "wbc_contrato")
    private Long wbcContract;
    
    @Column(name = "percentual_de_perda")
    private Double percentOfLoss;
    
    @Column(name = "fator_atendimento_carga")
    private Double factorAttendanceCharge;
    
    @Column(name = "proinfa")
    private Double proinfa;
    
    @Column(name = "data_criacao")
    private LocalDateTime createdAt;    
    
    @Column(name = "data_ultima_alteracao")
    private LocalDateTime lastUpdate;    
    
    
    @PrePersist
    public void generateCreatedAt(){
        this.createdAt = LocalDateTime.now();
    }
    
    
    @PreUpdate
    public void generateLastUpdate(){
        this.lastUpdate = LocalDateTime.now();
    }
    
}
