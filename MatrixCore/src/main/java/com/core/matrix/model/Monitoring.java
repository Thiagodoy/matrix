/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.model;

import com.core.matrix.annotation.ReportColumn;
import com.core.matrix.dto.MonitoringFilterDTO;
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
import javax.persistence.Transient;
import lombok.Data;

/**
 *
 * @author thiag
 */
@NamedNativeQuery(name = "Monitoring.filters", query = "SELECT distinct 'TASK' as type, ATIVIDADE_NO_MOMENTO as name FROM matrix.monitoramento_view where ATIVIDADE_NO_MOMENTO is not null \n"
        + "union all\n"
        + "SELECT distinct 'USER' as type, RESPONSAVEL as name FROM matrix.monitoramento_view where RESPONSAVEL is not null ",
        resultSetMapping = "filterMonitoringDTO")

@SqlResultSetMapping(name = "filterMonitoringDTO",
        classes = @ConstructorResult(
                targetClass = MonitoringFilterDTO.class,
                columns = {
                    @ColumnResult(name = "type", type = String.class)
                    ,
                    @ColumnResult(name = "name", type = String.class)
                }))

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
        + "            'CLIENTE' AS 'TIPO',            \n"
        + "            CASE ART.NAME_\n"
        + "                WHEN 'CÁLCULO DE MEDIÇÃO COMPLETO' THEN 'EM ANDAMENTO'\n"
        + "                WHEN 'INFORMAR HORAS FALTANTES' THEN 'PENDENTE'\n"
        + "                WHEN 'ARQUIVO INCONSISTENTE' THEN 'PENDENTE'\n"
        + "                WHEN 'PENDENTE DE UPLOAD' THEN 'PENDENTE'\n"
        + "                WHEN 'ANALISAR RECOMPRA' THEN 'EM ANDAMENTO'\n"
        + "                WHEN 'REVISAR INFORMAÇÕES' THEN 'EM ANDAMENTO'\n"
        + "                WHEN 'ANALISAR TAKE MÁXIMO' THEN 'EM ANDAMENTO'\n"
        + "                WHEN 'AGUARDAR O DE ACORDO DO CLIENTE' THEN 'EM ANDAMENTO'\n"
        + "                WHEN 'PENDENTE DE APROVAÇÃO DO VOLUME SOLICITADO - NÍVEL 1' THEN 'EM ANDAMENTO'\n"        
        + "                WHEN 'PENDENTE DE APROVAÇÃO DO VOLUME SOLICITADO - NÍVEL 2' THEN 'EM ANDAMENTO'\n"                   
        + "                ELSE 'FINALIZADO'\n"
        + "            END AS STATUS,\n"
        + "            COUNT(DISTINCT MCI.CNPJ) AS 'QUANTIDADE' \n"
        + "        FROM\n"
        + "            matrix.mtx_arquivo_de_medicao AS MAM\n"
        + "                LEFT JOIN\n"
        + "            activiti.ACT_RU_VARIABLE AS ARV ON MAM.ACT_ID_PROCESSO = ARV.PROC_INST_ID_\n"
        + "                AND ARV.NAME_ = '@Cliente'\n"
        + "                LEFT JOIN\n"
        + "            matrix.mtx_arquivo_de_medicao_resultado AS MAR ON MAM.ID_ARQUIVO_DE_MEDICAO = MAR.ID_ARQUIVO_DE_MEDICAO\n"
        + "                LEFT JOIN\n"
        + "            activiti.ACT_RU_TASK AS ART ON MAM.ACT_ID_PROCESSO = ART.PROC_INST_ID_\n"
        + "                LEFT JOIN\n"
        + "            matrix.mtx_contrato AS MCI ON MAM.WBC_CONTRATO = MCI.WBC_CONTRATO\n"
        + "                LEFT JOIN\n"
        + "            matrix.mtx_ponto_de_medicao_pro_infa AS MCP ON MAM.WBC_PONTO_DE_MEDICAO = MCP.WBC_PONTO_DE_MEDICAO\n"
        + "                AND MAM.MES = MCP.MES\n"
        + "                AND MAM.ANO = MCP.ANO\n"
        + "                LEFT JOIN\n"
        + "            matrix.mtx_contrato AS MCI2 ON MCI.wbc_codigo_contrato_rateio = MCI2.wbc_codigo_contrato\n"
        + "                LEFT JOIN\n"
        + "            matrix.mtx_aqruivo_de_medicao_recompra AS MMR ON MAR.id_arquivo_de_medicao = MMR.id_arquivo_de_medicao\n"
        + "        WHERE            \n"
        + "            MAM.MES = :mes AND MAM.ANO = :ano\n"
        + "        GROUP BY 1 , 2 \n"
        + "        UNION ALL SELECT \n"
        + "            'CONTRATO' AS 'TIPO',\n"
        + "            CASE ART.NAME_\n"
        + "                WHEN 'CÁLCULO DE MEDIÇÃO COMPLETO' THEN 'EM ANDAMENTO'\n"
        + "                WHEN 'INFORMAR HORAS FALTANTES' THEN 'PENDENTE'\n"
        + "                WHEN 'ARQUIVO INCONSISTENTE' THEN 'PENDENTE'\n"
        + "                WHEN 'PENDENTE DE UPLOAD' THEN 'PENDENTE'\n"
        + "                WHEN 'ANALISAR RECOMPRA' THEN 'EM ANDAMENTO'\n"
        + "                WHEN 'REVISAR INFORMAÇÕES' THEN 'EM ANDAMENTO'\n"
        + "                WHEN 'ANALISAR TAKE MÁXIMO' THEN 'EM ANDAMENTO'\n"
        + "                WHEN 'AGUARDAR O DE ACORDO DO CLIENTE' THEN 'EM ANDAMENTO'\n"
        + "                WHEN 'PENDENTE DE APROVAÇÃO DO VOLUME SOLICITADO - NÍVEL 1' THEN 'EM ANDAMENTO'\n"        
        + "                WHEN 'PENDENTE DE APROVAÇÃO DO VOLUME SOLICITADO - NÍVEL 2' THEN 'EM ANDAMENTO'\n"                   
        + "                ELSE 'FINALIZADO'\n"
        + "            END AS STATUS,\n"
        + "            COUNT(DISTINCT MAM.WBC_CONTRATO) AS 'QUANTIDADE'\n"
        + "        FROM\n"
        + "            matrix.mtx_arquivo_de_medicao AS MAM\n"
        + "                LEFT JOIN\n"
        + "            activiti.ACT_RU_VARIABLE AS ARV ON MAM.ACT_ID_PROCESSO = ARV.PROC_INST_ID_\n"
        + "                AND ARV.NAME_ = '@Cliente'\n"
        + "                LEFT JOIN\n"
        + "            matrix.mtx_arquivo_de_medicao_resultado AS MAR ON MAM.ID_ARQUIVO_DE_MEDICAO = MAR.ID_ARQUIVO_DE_MEDICAO\n"
        + "                LEFT JOIN\n"
        + "            activiti.ACT_RU_TASK AS ART ON MAM.ACT_ID_PROCESSO = ART.PROC_INST_ID_\n"
        + "                LEFT JOIN\n"
        + "            matrix.mtx_contrato AS MCI ON MAM.WBC_CONTRATO = MCI.WBC_CONTRATO\n"
        + "                LEFT JOIN\n"
        + "            matrix.mtx_ponto_de_medicao_pro_infa AS MCP ON MAM.WBC_PONTO_DE_MEDICAO = MCP.WBC_PONTO_DE_MEDICAO\n"
        + "                AND MAM.MES = MCP.MES\n"
        + "                AND MAM.ANO = MCP.ANO\n"
        + "                LEFT JOIN\n"
        + "            matrix.mtx_contrato AS MCI2 ON MCI.wbc_codigo_contrato_rateio = MCI2.wbc_codigo_contrato\n"
        + "                LEFT JOIN\n"
        + "            matrix.mtx_aqruivo_de_medicao_recompra AS MMR ON MAR.id_arquivo_de_medicao = MMR.id_arquivo_de_medicao\n"
        + "        WHERE\n"
        + "			MAM.MES = :mes AND MAM.ANO = :ano        \n"
        + "        GROUP BY 1 , 2 \n"
        + "        UNION ALL SELECT \n"
        + "            'PONTO' AS 'TIPO',\n"
        + "            CASE ART.NAME_\n"
        + "                WHEN 'CÁLCULO DE MEDIÇÃO COMPLETO' THEN 'EM ANDAMENTO'\n"
        + "                WHEN 'INFORMAR HORAS FALTANTES' THEN 'PENDENTE'\n"
        + "                WHEN 'ARQUIVO INCONSISTENTE' THEN 'PENDENTE'\n"
        + "                WHEN 'PENDENTE DE UPLOAD' THEN 'PENDENTE'\n"
        + "                WHEN 'ANALISAR RECOMPRA' THEN 'EM ANDAMENTO'\n"
        + "                WHEN 'REVISAR INFORMAÇÕES' THEN 'EM ANDAMENTO'\n"
        + "                WHEN 'ANALISAR TAKE MÁXIMO' THEN 'EM ANDAMENTO'\n"
        + "                WHEN 'AGUARDAR O DE ACORDO DO CLIENTE' THEN 'EM ANDAMENTO'\n"
        + "                WHEN 'PENDENTE DE APROVAÇÃO DO VOLUME SOLICITADO - NÍVEL 1' THEN 'EM ANDAMENTO'\n"        
        + "                WHEN 'PENDENTE DE APROVAÇÃO DO VOLUME SOLICITADO - NÍVEL 2' THEN 'EM ANDAMENTO'\n"                   
        + "                ELSE 'FINALIZADO'\n"
        + "            END AS STATUS,\n"
        + "            COUNT(DISTINCT MCP.WBC_PONTO_DE_MEDICAO) AS 'QUANTIDADE'\n"
        + "        FROM\n"
        + "            matrix.mtx_arquivo_de_medicao AS MAM\n"
        + "                LEFT JOIN\n"
        + "            activiti.ACT_RU_VARIABLE AS ARV ON MAM.ACT_ID_PROCESSO = ARV.PROC_INST_ID_\n"
        + "                AND ARV.NAME_ = '@Cliente'\n"
        + "                LEFT JOIN\n"
        + "            matrix.mtx_arquivo_de_medicao_resultado AS MAR ON MAM.ID_ARQUIVO_DE_MEDICAO = MAR.ID_ARQUIVO_DE_MEDICAO\n"
        + "                LEFT JOIN\n"
        + "            activiti.ACT_RU_TASK AS ART ON MAM.ACT_ID_PROCESSO = ART.PROC_INST_ID_\n"
        + "                LEFT JOIN\n"
        + "            matrix.mtx_contrato AS MCI ON MAM.WBC_CONTRATO = MCI.WBC_CONTRATO\n"
        + "                LEFT JOIN\n"
        + "            matrix.mtx_ponto_de_medicao_pro_infa AS MCP ON MAM.WBC_PONTO_DE_MEDICAO = MCP.WBC_PONTO_DE_MEDICAO\n"
        + "                AND MAM.MES = MCP.MES\n"
        + "                AND MAM.ANO = MCP.ANO\n"
        + "                LEFT JOIN\n"
        + "            matrix.mtx_contrato AS MCI2 ON MCI.wbc_codigo_contrato_rateio = MCI2.wbc_codigo_contrato\n"
        + "                LEFT JOIN\n"
        + "            matrix.mtx_aqruivo_de_medicao_recompra AS MMR ON MAR.id_arquivo_de_medicao = MMR.id_arquivo_de_medicao\n"
        + "        WHERE            \n"
        + "            MAM.MES = :mes AND MAM.ANO = :ano\n"
        + "        GROUP BY 1 , 2",
        resultSetMapping = "statusMonitoringDTO")

@Entity
@Table(schema = "matrix", name = "monitoramento_view_1")
@Data
@JsonIgnoreProperties(value = {"id"})
public class Monitoring implements Serializable {

    private static final long serialVersionUID = 359460498870162995L;

    @Id
    @Column(name = "ID")
    public Long id;

    @Column(name = "ID_TASK")
    public String taskId;

    @Column(name = "TEMPLATE")
    public String template;

    @ReportColumn(name = "INSTANCIA_DO_PROCESSO", position = 0, typeValue = String.class, typeReport = {"FULL"})
    @Column(name = "INSTANCIA_DO_PROCESSO")
    public String instanciaDoProcesso;

    @ReportColumn(name = "MES", position = 0, typeValue = String.class, typeReport = {"FULL"})
    @Column(name = "MES")
    public String mes;

    @ReportColumn(name = "ANO", position = 0, typeValue = String.class, typeReport = {"FULL"})
    @Column(name = "ANO")
    public String ano;

    @ReportColumn(name = "WBC_CONTRATO", position = 0, typeValue = String.class, typeReport = {"FULL"})
    @Column(name = "WBC_CONTRATO")
    public String wbcContrato;

    @Column(name = "WBC_CONTRATO_REFERENCIA")
    public String wbcContratoReferencia;

    @ReportColumn(name = "WBC_RATEIO", position = 0, typeValue = String.class, typeReport = {"FULL"})
    @Column(name = "WBC_RATEIO")
    public String wbcRateio;

    @ReportColumn(name = "PONTO_DE_MEDICAO", position = 0, typeValue = String.class, typeReport = {"FULL"})
    @Column(name = "PONTO_DE_MEDICAO")
    public String pontoDeMedicao;

    @ReportColumn(name = "MONTANTE_SCDE", position = 0, typeValue = String.class, typeReport = {"FULL"})
    @Column(name = "MONTANTE_SCDE")
    public String montanteScde;

    @ReportColumn(name = "INSTANCIA_DO_PROCESSO", position = 0, typeValue = String.class, typeReport = {"FULL"})
    @Column(name = "PROINFA")
    public String proinfa;

    @ReportColumn(name = "PROINFA", position = 0, typeValue = String.class, typeReport = {"FULL"})
    @Column(name = "FATOR_DE_CARGA")
    public String fatorDeCarga;

    @ReportColumn(name = "PERCENTUAL_DE_PERDA", position = 0, typeValue = String.class, typeReport = {"FULL"})
    @Column(name = "PERCENTUAL_DE_PERDA")
    public String percentualDeCarga;

    @ReportColumn(name = "SOLICITADO_BRUTO", position = 0, typeValue = String.class, typeReport = {"FULL"})
    @Column(name = "SOLICITADO_BRUTO")
    public String solicitadoBruto;

    @ReportColumn(name = "QUANTIDADE_CONTRATADA", position = 0, typeValue = String.class, typeReport = {"FULL"})
    @Column(name = "QAUNTIDADE_CONTRATADA")
    public String quantidadeContratada;

    @ReportColumn(name = "LIMITE_MIN", position = 0, typeValue = String.class, typeReport = {"FULL"})
    @Column(name = "LIMITE_MIN")
    public String limiteMin;

    @ReportColumn(name = "LIMITE_MAX", position = 0, typeValue = String.class, typeReport = {"FULL"})
    @Column(name = "LIMITE_MAX")
    public String limiteMax;

    @ReportColumn(name = "SOLICITADO_LIQUIDO", position = 0, typeValue = String.class, typeReport = {"FULL"})
    @Column(name = "SOLICITADO_LIQUIDO")
    public String solicitadoLiquido;

    @ReportColumn(name = "PRECO_CONTRATADO", position = 0, typeValue = String.class, typeReport = {"FULL"})
    @Column(name = "PRECO_CONTRATADO")
    public String precoContratado;

    @ReportColumn(name = "NOTIONAL_CONTRATADO", position = 0, typeValue = String.class, typeReport = {"FULL"})
    @Column(name = "NOTIONAL_CONTRATADO")
    public String notionalContratado;

    @ReportColumn(name = "TAKE", position = 0, typeValue = String.class, typeReport = {"FULL"})
    @Column(name = "TAKE")
    public String take;

    @ReportColumn(name = "VOLUME_RECOMPRA", position = 0, typeValue = String.class, typeReport = {"FULL"})
    @Column(name = "VOLUME_RECOMPRA")
    public String volumeRecompra;

    @ReportColumn(name = "PRECO_RECOMPRA", position = 0, typeValue = String.class, typeReport = {"FULL"})
    @Column(name = "PRECO_RECOMPRA")
    public String precoRecompra;

    @ReportColumn(name = "NOTIONAL_RECOMPRA", position = 0, typeValue = String.class, typeReport = {"FULL"})
    @Column(name = "NOTIONAL_RECOMPRA")
    public String notionalRecompra;

    @ReportColumn(name = "NOVO_FATURAMENTO", position = 0, typeValue = String.class, typeReport = {"FULL"})
    @Column(name = "NOVO_FATURAMENTO")
    public String novoFaturamento;

    @ReportColumn(name = "NOVO_PRECO", position = 0, typeValue = String.class, typeReport = {"FULL"})
    @Column(name = "NOVO_PRECO")
    public String novoPreco;

    @ReportColumn(name = "NOVO_NOTIONAL", position = 0, typeValue = String.class, typeReport = {"FULL"})
    @Column(name = "NOVO_NOTIONAL")
    public String novoNotional;

    @ReportColumn(name = "EMPRESA", position = 0, typeValue = String.class, typeReport = {"FULL"})
    @Column(name = "EMPRESA")
    public String empresa;

    @ReportColumn(name = "STATUS", position = 0, typeValue = String.class, typeReport = {"FULL"})
    @Column(name = "STATUS")
    public String status;

    @ReportColumn(name = "ATIVIDADE_NO_MOMENTO", position = 0, typeValue = String.class, typeReport = {"FULL"})
    @Column(name = "ATIVIDADE_NO_MOMENTO")
    public String atividadeNoMomento;

    @ReportColumn(name = "RESPONSAVEL", position = 0, typeValue = String.class, typeReport = {"FULL"})
    @Column(name = "RESPONSAVEL")
    public String responsavel;

    @ReportColumn(name = "VALOR_AJUSTADO", position = 0, typeValue = String.class, typeReport = {"FULL"})
    @Column(name = "VALOR_AJUSTADO")
    public Double valorAjustado;
    
    @Transient
    public Double valorEsperadoWbc;

}
