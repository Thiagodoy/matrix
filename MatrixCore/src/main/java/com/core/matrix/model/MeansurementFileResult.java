/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.model;

import com.core.matrix.dto.MeansurementFileResultStatusDTO;
import com.core.matrix.wbc.dto.ContractWbcInformationDTO;
import com.google.common.base.Optional;
import java.time.LocalDateTime;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedNativeQuery;
import javax.persistence.PrePersist;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 *
 * @author thiag
 */
@SqlResultSetMapping(name = "statusBillingDTO",
        classes = @ConstructorResult(
                targetClass = MeansurementFileResultStatusDTO.class,
                columns = {
                    @ColumnResult(name = "act_id_process", type = String.class)
                    ,
                    @ColumnResult(name = "year", type = Long.class)
                    ,
                    @ColumnResult(name = "month", type = Long.class)
                    ,
                    @ColumnResult(name = "wbc_contrato", type = Long.class)
                    ,
                    @ColumnResult(name = "apelido", type = String.class)
                    ,
                    @ColumnResult(name = "montante", type = Double.class)
                    ,
                    @ColumnResult(name = "wbc_ponto_de_medicao", type = String.class)
                    ,
                    @ColumnResult(name = "rateio", type = String.class)
                    ,
                    @ColumnResult(name = "status", type = String.class)
                    ,
                    @ColumnResult(name = "responsavel", type = String.class)
                    ,
                    @ColumnResult(name = "exportado", type = Boolean.class)
                    ,
                    @ColumnResult(name = "checkbox", type = String.class)                   
                    
                }))

@NamedNativeQuery(name = "MeansurementFileResult.getStatusBilling",
        query = "SELECT \n"
        + "    x.act_id_process,\n"
        + "    x.year,\n"
        + "    x.month,\n"
        + "    x.wbc_contrato,\n"
        + "    x.apelido,\n"
        + "    x.montante,\n"
        + "    x.wbc_ponto_de_medicao,\n"
        + "    (CASE\n"
        + "        WHEN x.rateio > 0 THEN 'SIM'\n"
        + "        ELSE 'NÃO'\n"
        + "    END) AS rateio,\n"
        + "    x.status,\n"
        + "    x.responsavel,\n"
        + "    x.exportado,\n"
        + "    IF((x.rateio = 0 AND x.contrato_pai = '00')\n"
        + "            OR (x.rateio > 0 AND x.contrato_pai = '01'),\n"
        + "        'true',\n"
        + "        'false') AS checkbox\n"
        + "FROM\n"
        + "    (SELECT 		\n"
        + "        a.apelido,\n"
        + "            a.nome_empresa,\n"
        + "            a.wbc_contrato,\n"
        + "            MONTH(r.data_criacao) AS month,\n"
        + "            YEAR(r.data_criacao) AS year,\n"
        + "            (CASE\n"
        + "                WHEN r.montante_liquido_ajustado IS NOT NULL THEN r.montante_liquido_ajustado\n"
        + "                ELSE r.montante_liquido\n"
        + "            END) AS montante,\n"
        + "            (CASE\n"
        + "                WHEN r.contrato_pai = '01' THEN a.wbc_codigo_contrato\n"
        + "                WHEN a.wbc_codigo_contrato_rateio IS NULL THEN 0\n"
        + "                ELSE a.wbc_codigo_contrato_rateio\n"
        + "            END) AS rateio,\n"
        + "            a.flat,\n"
        + "            a.unidade_consumidora,\n"
        + "            c.wbc_ponto_de_medicao,\n"
        + "            IF(id_aquivo_de_medicao_resultado, 'APROVADO', 'PENDENTE') AS status,\n"
        + "            r.exportado,\n"
        + "            r.act_id_process,\n"
        + "            IFNULL(r.contrato_pai, '00') AS contrato_pai,\n"
        + "            CONCAT(u.FIRST_, ' ', u.LAST_) AS responsavel\n"
        + "    FROM\n"
        + "        mtx_contrato a\n"
        + "    LEFT JOIN mtx_ponto_contrato b ON a.id_contrato = b.id_contrato\n"
        + "    LEFT JOIN mtx_ponto_de_medicao c ON b.id_ponto_de_medicao = c.id_ponto_de_medicao\n"
        + "    LEFT JOIN mtx_arquivo_de_medicao_resultado r ON a.wbc_contrato = r.wbc_contrato\n"
        + "    LEFT JOIN activiti.act_hi_varinst v ON v.PROC_INST_ID_ = r.act_id_process\n"
        + "    INNER JOIN activiti.act_id_user u ON u.ID_ = v.TEXT_\n"
        + "        AND v.NAME_ = 'created_by'\n"
        + "    WHERE\n"
        + "        r.data_criacao BETWEEN :start AND :end\n"
        + "            AND NOT EXISTS( SELECT \n"
        + "                1\n"
        + "            FROM\n"
        + "                mtx_aqruivo_de_medicao_recompra rr\n"
        + "            WHERE\n"
        + "                rr.act_id_processo = r.act_id_process)) AS x\n"
        + "WHERE\n"
        + "    x.status = 'APROVADO'\n"
        + "ORDER BY x.rateio , x.contrato_pai DESC",
        resultSetMapping = "statusBillingDTO")

@Entity
@Table(name = "mtx_arquivo_de_medicao_resultado")
@Data
@EqualsAndHashCode(of = {"wbcContract"})
@NoArgsConstructor
public class MeansurementFileResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_aquivo_de_medicao_resultado")
    private Long id;

    @Column(name = "id_arquivo_de_medicao")
    private Long meansurementFileId;

    @Column(name = "percentual_de_perda")
    private Double percentLoss;

    @Column(name = "fator_atendimento_carga")
    private Double factorAtt;

    @Column(name = "proinfa")
    private Double proinfa;

    @Column(name = "montante_scde")
    private Double amountScde;

    @Column(name = "montante_bruto")
    private Double amountBruto;

    @Column(name = "montante_liquido")
    private Double amountLiquido;

    @Column(name = "limite_minimo")
    private Double limitMin;

    @Column(name = "limite_maximo")
    private Double limitMax;

    @Column(name = "quantidade_contratada")
    private Double qtdHired;

    @Column(name = "quantidade_contratada_minima")
    private Double qtdHiredMin;

    @Column(name = "quantidade_contratada_maxima")
    private Double qtdHiredMax;

    @Column(name = "act_id_process")
    private String idProcess;

    @Column(name = "ponto_de_medicao")
    private String meansurementPoint;

    @Column(name = "wbc_contrato")
    private Long wbcContract;

    @Column(name = "nome_empresa")
    private String nameCompany;

    @Column(name = "nome_fantasia")
    private String nickNameCompany;

    @Column(name = "contrato_pai")
    private Long contractParent;

    @Column(name = "montante_liquido_ajustado")
    private Double amountLiquidoAdjusted;

    @Column(name = "justificativa")
    private String justify;

    @Column(name = "preco_contratado")
    private Double price;

    @Column(name = "wbc_submercado")
    private Integer wbcSubmercado;

    @Column(name = "wbc_perfilCCEE")
    private Integer wbcPerfilCCEE;

    @Column(name = "exportado")
    private boolean isExported;

    @Column(name = "data_criacao")
    private LocalDateTime createdAt;

    public MeansurementFileResult(ContractWbcInformationDTO informationDTO, String idProcess) {

        this.qtdHiredMin = Optional.fromNullable(informationDTO).isPresent() ? informationDTO.getQtdHiredMin() : 0D;
        this.qtdHiredMax = Optional.fromNullable(informationDTO).isPresent() ? informationDTO.getQtdHiredMax() : 0D;
        this.qtdHired = Optional.fromNullable(informationDTO).isPresent() ? informationDTO.getQtdHired() : 0D;
        this.idProcess = idProcess;
        this.limitMin = Optional.fromNullable(informationDTO).isPresent() ? informationDTO.getLimitMin() : 0D;
        this.limitMax = Optional.fromNullable(informationDTO).isPresent() ? informationDTO.getLimitMax() : 0D;
        this.price = Optional.fromNullable(informationDTO).isPresent() ? informationDTO.getPrice() : 0D;

    }

    @PrePersist
    public void generateDate() {
        this.createdAt = LocalDateTime.now();
    }

    public void update(MeansurementFileResult result) {

        if (Optional.fromNullable(result.getAmountLiquidoAdjusted()).isPresent() && !result.getAmountLiquidoAdjusted().equals(this.amountLiquidoAdjusted)) {
            this.amountLiquidoAdjusted = result.getAmountLiquidoAdjusted();
        }

        if (Optional.fromNullable(result.getJustify()).isPresent() && !result.getJustify().equals(this.justify)) {
            this.justify = result.getJustify();
        }

    }

}
