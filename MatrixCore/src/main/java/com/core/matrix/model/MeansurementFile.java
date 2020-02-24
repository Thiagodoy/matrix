/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.model;

import com.core.matrix.dto.MeansurementFileStatusDTO;
import com.core.matrix.utils.MeansurementFileStatus;
import com.core.matrix.utils.MeansurementFileType;
import com.core.matrix.wbc.dto.ContractDTO;
import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedNativeQuery;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author thiag
 */


@SqlResultSetMapping(name = "statusDTO", 
        classes = @ConstructorResult(
                targetClass = MeansurementFileStatusDTO.class,
                columns = {
                        @ColumnResult(name = "Status", type = String.class),
                        @ColumnResult(name = "qtd", type = Long.class),                                                       
                    
                }))

@NamedNativeQuery(name = "MeansurementFile.getStatus",
        query = "select 'RECEIVED' as status, count(1) as qtd from matrix.mtx_arquivo_de_medicao a where a.mes = :month and a.ano = :year\n" +
                "union all\n" +
                "select status , count(1) from matrix.mtx_arquivo_de_medicao a where a.mes = :month and a.ano = :year group by status ",
        resultSetMapping = "statusDTO")

@Entity
@Table(name = "mtx_arquivo_de_medicao")
@Data
@NoArgsConstructor
public class MeansurementFile {

    @Id
    @Column(name = "id_arquivo_de_medicao")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "wbc_contrato")
    private Long wbcContract;
    
    @Column(name = "wbc_ponto_de_medicao")
    private String meansurementPoint;
    
    @Column(name = "act_id_processo")
    private String processInstanceId;
    
    @Column(name = "mes")
    private Long month;

    @Column(name = "ano")
    private Long year;

    @Column(name = "arquivo")
    private String file;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private MeansurementFileStatus status;

    @Column(name = "act_id_usuario")
    private String user;

    @Column(name = "data_criacao")
    private LocalDateTime createdAt;

    @Column(name = "data_ultima_alteracao")
    private LocalDateTime updatedAt;

    @Column(name = "tipo_arquivo")
    @Enumerated(EnumType.STRING)
    private MeansurementFileType type;
    
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.REMOVE }, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "id_arquivo_de_medicao")
    private List<MeansurementFileDetail>details;    
    
    
    @PrePersist
    public void generateCreatedAt(){
        this.createdAt = LocalDateTime.now();
    }
    
    
    public MeansurementFile(ContractDTO dTO, String processInstance, String meansurementPoint){        
        this.status  = MeansurementFileStatus.FILE_PENDING;
        this.wbcContract = Long.parseLong(dTO.getSNrContrato());
        this.meansurementPoint = meansurementPoint;
        this.processInstanceId = processInstance;
    }
    
   
    

}
