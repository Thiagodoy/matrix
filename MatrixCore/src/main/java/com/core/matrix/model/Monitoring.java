/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.model;

import com.core.matrix.dto.MonitoringStatusDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedNativeQuery;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author thiag
 */
@SqlResultSetMapping(name = "statusMonitoringDTO",
        classes = @ConstructorResult(
                targetClass = MonitoringStatusDTO.class,
                columns = {
                    @ColumnResult(name = "TIPO", type = String.class)
                    ,
                    @ColumnResult(name = "STATUS", type = String.class)
                    ,
                    @ColumnResult(name = "QUANTIDADE", type = Long.class)

                }))

@NamedNativeQuery(name = "Monitoring.status",
        query = "SELECT \n"
        + "    'CLIENTE' AS 'TIPO',\n"
        + "    CASE MAM.STATUS\n"
        + "        WHEN 'SUCCESS' THEN 'EM ANDAMENTO'\n"
        + "        WHEN 'FILE_PENDING' THEN 'PENDENTE'\n"
        + "        WHEN 'APPROVED' THEN 'FINALIZADO'\n"
        + "    END AS STATUS,\n"
        + "    COUNT(DISTINCT CASE\n"
        + "            WHEN MAR.NOME_EMPRESA IS NULL THEN ARV.TEXT_\n"
        + "            WHEN MAR.NOME_EMPRESA IS NOT NULL THEN MAR.NOME_EMPRESA\n"
        + "        END) AS 'QUANTIDADE'\n"
        + "FROM\n"
        + "    matrix.mtx_arquivo_de_medicao AS MAM\n"
        + "        LEFT JOIN\n"
        + "    activiti.act_ru_variable AS ARV ON MAM.ACT_ID_PROCESSO = ARV.PROC_INST_ID_\n"
        + "        AND ARV.NAME_ = '@Cliente'\n"
        + "        LEFT JOIN\n"
        + "    matrix.mtx_arquivo_de_medicao_resultado AS MAR ON MAM.ID_ARQUIVO_DE_MEDICAO = MAR.ID_ARQUIVO_DE_MEDICAO\n"
        + "        LEFT JOIN\n"
        + "    activiti.act_ru_task AS ART ON MAM.ACT_ID_PROCESSO = ART.PROC_INST_ID_\n"
        + "        LEFT JOIN\n"
        + "    matrix.mtx_contrato_informacao_complementar AS MCI ON MAM.WBC_CONTRATO = MCI.WBC_CONTRATO\n"
        + "        LEFT JOIN\n"
        + "    matrix.mtx_contrato_proinfa AS MCP ON MAM.WBC_CONTRATO = MCP.WBC_CONTRATO\n"
        + "        AND MAM.WBC_PONTO_DE_MEDICAO = MCP.WBC_PONTO_DE_MEDICAO\n"
        + "        AND MAM.MES = MCP.MES\n"
        + "        AND MAM.ANO = MCP.ANO\n"
        + "        LEFT JOIN\n"
        + "    matrix.mtx_contrato_informacao_complementar AS MCI2 ON MCI.wbc_codigo_contrato_rateio = MCI2.wbc_codigo_contrato\n"
        + "        LEFT JOIN\n"
        + "    matrix.mtx_aqruivo_de_medicao_recompra AS MMR ON MAR.id_arquivo_de_medicao = MMR.id_arquivo_de_medicao\n"
        + "WHERE\n"
        + "    MAM.MES = :mes AND MAM.ANO = :ano\n"
        + "GROUP BY 1 , 2 \n"
        + "UNION ALL SELECT \n"
        + "    'CONTRATO' AS 'TIPO',\n"
        + "    CASE MAM.STATUS\n"
        + "        WHEN 'SUCCESS' THEN 'EM ANDAMENTO'\n"
        + "        WHEN 'FILE_PENDING' THEN 'PENDENTE'\n"
        + "        WHEN 'APPROVED' THEN 'FINALIZADO'\n"
        + "    END AS STATUS,\n"
        + "    COUNT(DISTINCT MAM.WBC_CONTRATO) AS 'QUANTIDADE'\n"
        + "FROM\n"
        + "    matrix.mtx_arquivo_de_medicao AS MAM\n"
        + "        LEFT JOIN\n"
        + "    activiti.act_ru_variable AS ARV ON MAM.ACT_ID_PROCESSO = ARV.PROC_INST_ID_\n"
        + "        AND ARV.NAME_ = '@Cliente'\n"
        + "        LEFT JOIN\n"
        + "    matrix.mtx_arquivo_de_medicao_resultado AS MAR ON MAM.ID_ARQUIVO_DE_MEDICAO = MAR.ID_ARQUIVO_DE_MEDICAO\n"
        + "        LEFT JOIN\n"
        + "    activiti.act_ru_task AS ART ON MAM.ACT_ID_PROCESSO = ART.PROC_INST_ID_\n"
        + "        LEFT JOIN\n"
        + "    matrix.mtx_contrato_informacao_complementar AS MCI ON MAM.WBC_CONTRATO = MCI.WBC_CONTRATO\n"
        + "        LEFT JOIN\n"
        + "    matrix.mtx_contrato_proinfa AS MCP ON MAM.WBC_CONTRATO = MCP.WBC_CONTRATO\n"
        + "        AND MAM.WBC_PONTO_DE_MEDICAO = MCP.WBC_PONTO_DE_MEDICAO\n"
        + "        AND MAM.MES = MCP.MES\n"
        + "        AND MAM.ANO = MCP.ANO\n"
        + "        LEFT JOIN\n"
        + "    matrix.mtx_contrato_informacao_complementar AS MCI2 ON MCI.wbc_codigo_contrato_rateio = MCI2.wbc_codigo_contrato\n"
        + "        LEFT JOIN\n"
        + "    matrix.mtx_aqruivo_de_medicao_recompra AS MMR ON MAR.id_arquivo_de_medicao = MMR.id_arquivo_de_medicao\n"
        + "WHERE\n"
        + "    MAM.MES = :mes AND MAM.ANO = :ano\n"
        + "GROUP BY 1 , 2 \n"
        + "UNION ALL SELECT \n"
        + "    'PONTO' AS 'TIPO',\n"
        + "    CASE MAM.STATUS\n"
        + "        WHEN 'SUCCESS' THEN 'EM ANDAMENTO'\n"
        + "        WHEN 'FILE_PENDING' THEN 'PENDENTE'\n"
        + "        WHEN 'APPROVED' THEN 'FINALIZADO'\n"
        + "    END AS STATUS,\n"
        + "    COUNT(DISTINCT MAM.WBC_PONTO_DE_MEDICAO) AS 'QUANTIDADE'\n"
        + "FROM\n"
        + "    matrix.mtx_arquivo_de_medicao AS MAM\n"
        + "        LEFT JOIN\n"
        + "    activiti.act_ru_variable AS ARV ON MAM.ACT_ID_PROCESSO = ARV.PROC_INST_ID_\n"
        + "        AND ARV.NAME_ = '@Cliente'\n"
        + "        LEFT JOIN\n"
        + "    matrix.mtx_arquivo_de_medicao_resultado AS MAR ON MAM.ID_ARQUIVO_DE_MEDICAO = MAR.ID_ARQUIVO_DE_MEDICAO\n"
        + "        LEFT JOIN\n"
        + "    activiti.act_ru_task AS ART ON MAM.ACT_ID_PROCESSO = ART.PROC_INST_ID_\n"
        + "        LEFT JOIN\n"
        + "    matrix.mtx_contrato_informacao_complementar AS MCI ON MAM.WBC_CONTRATO = MCI.WBC_CONTRATO\n"
        + "        LEFT JOIN\n"
        + "    matrix.mtx_contrato_proinfa AS MCP ON MAM.WBC_CONTRATO = MCP.WBC_CONTRATO\n"
        + "        AND MAM.WBC_PONTO_DE_MEDICAO = MCP.WBC_PONTO_DE_MEDICAO\n"
        + "        AND MAM.MES = MCP.MES\n"
        + "        AND MAM.ANO = MCP.ANO\n"
        + "        LEFT JOIN\n"
        + "    matrix.mtx_contrato_informacao_complementar AS MCI2 ON MCI.wbc_codigo_contrato_rateio = MCI2.wbc_codigo_contrato\n"
        + "        LEFT JOIN\n"
        + "    matrix.mtx_aqruivo_de_medicao_recompra AS MMR ON MAR.id_arquivo_de_medicao = MMR.id_arquivo_de_medicao\n"
        + "WHERE\n"
        + "    MAM.MES = :mes AND MAM.ANO = :ano\n"
        + "GROUP BY 1 , 2",
        resultSetMapping = "statusMonitoringDTO")

@Entity
@Table(schema = "matrix", name = "monitoramento_view")
@Data
@JsonIgnoreProperties(value = {"id"})
public class Monitoring implements Serializable {

    private static final long serialVersionUID = 359460498870162995L;

    @Id
    @Column(name = "ID")
    private Long id;    
    
    @Column(name = "ID_TASK")
    private String taskId;
    
    @Column(name = "TEMPLATE")
    private String template;

    @Column(name = "INSTANCIA_DO_PROCESSO")
    private String instanciaDoProcesso;

    @Column(name = "MES")
    private String mes;

    @Column(name = "ANO")
    private String ano;

    @Column(name = "WBC_CONTRATO")
    private String wbcContrato;

    @Column(name = "WBC_RATEIO")
    private String wbcRateio;

    @Column(name = "PONTO_DE_MEDICAO")
    private String pontoDeMedicao;

    @Column(name = "MONTANTE_SCDE")
    private String montanteScde;

    @Column(name = "PROINFA")
    private String proinfa;

    @Column(name = "FATOR_DE_CARGA")
    private String fatorDeCarga;

    @Column(name = "PERCENTUAL_DE_PERDA")
    private String percentualDeCarga;

    @Column(name = "SOLICITADO_BRUTO")
    private String solicitadoBruto;

    @Column(name = "QAUNTIDADE_CONTRATADA")
    private String quantidadeContratada;

    @Column(name = "LIMITE_MIN")
    private String limiteMin;

    @Column(name = "LIMITE_MAX")
    private String limiteMax;

    @Column(name = "SOLICITADO_LIQUIDO")
    private String solicitadoLiquido;

    @Column(name = "PRECO_CONTRATADO")
    private String precoContratado;

    @Column(name = "NOTIONAL_CONTRATADO")
    private String notionalContratado;

    @Column(name = "TAKE")
    private String take;

    @Column(name = "VOLUME_RECOMPRA")
    private String volumeRecompra;

    @Column(name = "PRECO_RECOMPRA")
    private String precoRecompra;

    @Column(name = "NOTIONAL_RECOMPRA")
    private String notionalRecompra;

    @Column(name = "NOVO_FATURAMENTO")
    private String novoFaturamento;

    @Column(name = "NOVO_PRECO")
    private String novoPreco;

    @Column(name = "NOVO_NOTIONAL")
    private String novoNotional;

    @Column(name = "EMPRESA")
    private String empresa;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "ATIVIDADE_NO_MOMENTO")
    private String atividadeNoMomento;

    @Column(name = "RESPONSAVEL")
    private String responsavel;

}
