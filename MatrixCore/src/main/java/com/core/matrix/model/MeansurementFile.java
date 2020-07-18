/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.model;

import com.core.matrix.dto.ContractUnBillingDTO;
import com.core.matrix.dto.FileStatusDTO;
import com.core.matrix.dto.MeansurementFileStatusDTO;
import static com.core.matrix.utils.Constants.TABLE_SEQUENCES;
import com.core.matrix.utils.MeansurementFileStatus;
import com.core.matrix.utils.MeansurementFileType;
import com.core.matrix.wbc.dto.ContractDTO;
import java.io.Serializable;
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
                    @ColumnResult(name = "Status", type = String.class)
                    ,
                    @ColumnResult(name = "qtd", type = Long.class)
                }))

@SqlResultSetMapping(name = "statusBilling",
        classes = @ConstructorResult(
                targetClass = FileStatusDTO.class,
                columns = {
                    @ColumnResult(name = "count", type = Long.class)
                    ,
                    @ColumnResult(name = "status", type = String.class)
                }))

@SqlResultSetMapping(name = "statusLote",
        classes = @ConstructorResult(
                targetClass = ProcessStatusLote.class,
                columns = {
                    @ColumnResult(name = "taskName", type = String.class)
                    ,
                    @ColumnResult(name = "contract", type = Long.class)
                    ,
                    @ColumnResult(name = "point", type = String.class)
                    ,
                    @ColumnResult(name = "nickname", type = String.class)
                    ,
                    @ColumnResult(name = "status", type = String.class)
                    ,
                    @ColumnResult(name = "processInstance", type = String.class)
                }))
@SqlResultSetMapping(name = "contractUnbillingResult",
        classes = @ConstructorResult(targetClass = ContractUnBillingDTO.class, columns = {
    @ColumnResult(name = "wbc_contrato", type = Long.class)
}))

@NamedNativeQuery(name = "MeansurementFile.getStatus",
        query = "select 'RECEIVED' as status, count(1) as qtd from matrix.mtx_arquivo_de_medicao a where a.mes = :month and a.ano = :year\n"
        + "union all\n"
        + "select status , count(1) from matrix.mtx_arquivo_de_medicao a where a.mes = :month and a.ano = :year group by status ",
        resultSetMapping = "statusDTO")

@NamedNativeQuery(name = "MeansurementFile.getStatusBilling", query = "select x.status, count(1) as count from (SELECT ru.NAME_ as taskName,\n"
        + "        a.wbc_contrato as contract,\n"
        + "        a.wbc_ponto_de_medicao as point,\n"
        + "        a.empresa_apelido as nickname,\n"
        + "        (CASE\n"
        + "            WHEN (ru.NAME_ = 'RESULTADO DO CÁLCULO DE MEDIÇÃO') THEN 'PROCESSADO COM SUCESSO'\n"
        + "            WHEN\n"
        + "                (ru.NAME_ = 'AJUSTAR DADOS DE MEDIÇÃO'\n"
        + "                    OR ru.NAME_ = 'VISUALIZAR ERROS DE VALIDAÇÃO')\n"
        + "            THEN\n"
        + "                'PENDENTES'\n"
        + "            ELSE 'NÃO CARREGADO'\n"
        + "        END) AS status,\n"
        + "        a.act_id_processo as processInstance\n"
        + "         FROM\n"
        + "        mtx_arquivo_de_medicao a\n"
        + "            INNER JOIN\n"
        + "        activiti.ACT_RU_TASK ru ON a.act_id_processo = ru.PROC_INST_ID_\n"
        + "        where a.act_id_processo in :process ) as x group by x.status",
        resultSetMapping = "statusBilling")

@NamedNativeQuery(name = "MeansurementFile.findByProcessInstanceIdIn",
        query = "SELECT \n"
        + "    ru.NAME_ as taskName,\n"
        + "    a.wbc_contrato as contract,\n"
        + "    a.wbc_ponto_de_medicao as point,\n"
        + "    a.empresa_apelido as nickname,\n"
        + "    (CASE\n"
        + "        WHEN (ru.NAME_ = 'RESULTADO DO CÁLCULO DE MEDIÇÃO') THEN 'PROCESSADO COM SUCESSO'\n"
        + "        WHEN\n"
        + "            (ru.NAME_ = 'AJUSTAR DADOS DE MEDIÇÃO'\n"
        + "                OR ru.NAME_ = 'VISUALIZAR ERROS DE VALIDAÇÃO')\n"
        + "        THEN\n"
        + "            'PENDENTES'\n"
        + "        ELSE 'NÃO CARREGADO'\n"
        + "    END) AS status,\n"
        + "    a.act_id_processo as processInstance\n"
        + " FROM\n"
        + "    mtx_arquivo_de_medicao a\n"
        + "        LEFT JOIN\n"
        + "    activiti.ACT_RU_TASK ru ON a.act_id_processo = ru.PROC_INST_ID_\n"
        + "    where a.act_id_processo in :process",
        resultSetMapping = "statusLote")

@NamedNativeQuery(name = "MeansurementFile.contractUnbilling",
        query = "select distinct wbc_contrato from mtx_arquivo_de_medicao where wbc_contrato in :contracts and mes = :month and ano = :year",
        resultSetMapping = "contractUnbillingResult")

@Entity
@Table(name = "mtx_arquivo_de_medicao")
@Data
@NoArgsConstructor
public class MeansurementFile implements Serializable {

    private static final long serialVersionUID = 5548972239473582793L;

    @Id
    @Column(name = "id_arquivo_de_medicao")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = TABLE_SEQUENCES)
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

    @Column(name = "empresa_nome")
    private String companyName;

    @Column(name = "empresa_apelido")
    private String nickname;

    @Column(name = "data_criacao")
    private LocalDateTime createdAt;

    @Column(name = "data_ultima_alteracao")
    private LocalDateTime updatedAt;

    @Column(name = "tipo_arquivo")
    @Enumerated(EnumType.STRING)
    private MeansurementFileType type;

    @OneToMany(cascade = {CascadeType.PERSIST,CascadeType.REMOVE}, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "id_arquivo_de_medicao")
    private List<MeansurementFileDetail> details;

    @PrePersist
    public void generateCreatedAt() {
        this.createdAt = LocalDateTime.now();
    }

    public MeansurementFile(ContractDTO dTO, String processInstance, String meansurementPoint) {
        this.status = MeansurementFileStatus.FILE_PENDING;
        this.wbcContract = Long.parseLong(dTO.getSNrContrato());
        this.meansurementPoint = meansurementPoint;
        this.processInstanceId = processInstance;
    }

}
