/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.model;

import com.core.matrix.dto.MonitoringContractDTO;
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
@SqlResultSetMapping(name = "statusMonitoContactDTO",
        classes = @ConstructorResult(
                targetClass = MonitoringContractDTO.class,
                columns = {
                    @ColumnResult(name = "wbc_contrato", type = Long.class)
                    ,
                    @ColumnResult(name = "nome", type = String.class)
                    ,
                    @ColumnResult(name = "id_tarefa", type = String.class)
                    ,
                    @ColumnResult(name = "template", type = String.class)
                    ,
                    @ColumnResult(name = "tarefa", type = String.class)
                    ,
                    @ColumnResult(name = "horas_faltantes", type = Long.class)
                    ,
                    @ColumnResult(name = "consumo", type = Double.class)
                    ,
                    @ColumnResult(name = "rateio", type = Long.class)
                    ,
                    @ColumnResult(name = "status", type = String.class)
                }))

@NamedNativeQuery(name = "MonitoringPoint.getStatusByContract",
        query = "SELECT \n"
        + "    xx.*\n"
        + "FROM\n"
        + "    (SELECT \n"
        + "        f.wbc_contrato,\n"
        + "            f.nome_fantasia AS nome,\n"
        + "            '' AS id_tarefa,\n"
        + "            '' AS template,\n"
        + "            '' AS tarefa,\n"
        + "            (- 1) AS horas_faltantes,\n"
        + "            (- 1) AS consumo,\n"
        + "            f.wbc_codigo_contrato AS rateio,\n"
        + "            '' AS status\n"
        + "    FROM\n"
        + "        matrix.mtx_contrato f\n"
        + "    WHERE\n"
        + "        f.wbc_codigo_contrato IN (SELECT DISTINCT\n"
        + "                x.wbc_codigo_contrato_rateio\n"
        + "            FROM\n"
        + "                matrix.mtx_arquivo_de_medicao a\n"
        + "            INNER JOIN matrix.mtx_contrato x ON x.wbc_contrato = a.wbc_contrato\n"
        + "                AND a.mes = :month AND a.ano = :year\n"
        + "                AND IFNULL(x.wbc_codigo_contrato_rateio, 0) > 0) UNION ALL SELECT \n"
        + "        a.wbc_contrato,\n"
        + "            a.empresa_apelido AS nome,\n"
        + "            c.ID_ AS id_tarefa,\n"
        + "            c.FORM_KEY_ AS template,\n"
        + "            c.NAME_ AS tarefa,\n"
        + "            SUM(IF(b.status != 'SUCCESS', 1, 0)) AS horas_faltantes,\n"
        + "            SUM(IFNULL(b.reativa_consumo, 0)) AS consumo,\n"
        + "            x.wbc_codigo_contrato_rateio AS rateio,\n"
        + "            (CASE\n"
        + "                WHEN\n"
        + "                    (c.NAME_ = 'AJUSTAR DADOS DE MEDIÇÃO'\n"
        + "                        OR c.NAME_ = 'VISUALIZAR ERROS DE VALIDAÇÃO'\n"
        + "                        OR c.NAME_ = 'APROVAR AJUSTE NO CÁLCULO DE MEDIÇÃO'\n"
        + "                        OR c.NAME_ = 'REALIZAR O UPLOAD DO ARQUIVO CORRIGIDO'\n"
        + "                        OR c.NAME_ = 'REALIZAR UPLOAD DO ARQUIVO DE MEDIÇÃO')\n"
        + "                THEN\n"
        + "                    'Com pendência'\n"
        + "                ELSE 'Sem pendência'\n"
        + "            END) AS status\n"
        + "    FROM\n"
        + "        matrix.mtx_arquivo_de_medicao a\n"
        + "    LEFT JOIN matrix.mtx_arquivo_de_medicao_detalhe b ON a.id_arquivo_de_medicao = b.id_arquivo_de_medicao\n"
        + "    INNER JOIN matrix.mtx_contrato x ON x.wbc_contrato = a.wbc_contrato\n"
        + "    LEFT JOIN activiti.ACT_RU_TASK c ON a.act_id_processo = c.PROC_INST_ID_\n"
        + "    INNER JOIN activiti.ACT_HI_PROCINST d ON a.act_id_processo = d.PROC_INST_ID_\n"
        + "        AND d.END_TIME_ IS NULL\n"
        + "    WHERE\n"
        + "        a.mes = :month AND a.ano = :year\n"
        + "    GROUP BY a.wbc_contrato , x.wbc_codigo_contrato_rateio , a.empresa_apelido , c.ID_ , c.FORM_KEY_ , c.NAME_) xx\n"
        + "ORDER BY xx.rateio DESC , xx.horas_faltantes ASC",
        resultSetMapping = "statusMonitoContactDTO")

@Entity
@Table(name = "monitoramento_ponto_view")
@Data
public class MonitoringPoint implements Model<MonitoringPoint> {

    @Id
    @Column(name = "id_arquivo_de_medicao")
    private Long id;

    @Column(name = "act_id_processo")
    private String processInstanceId;

    @Column(name = "wbc_ponto_de_medicao")
    private String point;

    @Column(name = "empresa_apelido")
    private String nickname;

    @Column(name = "horas_faltantes")
    private Long hours;

    @Column(name = "consumo")
    private Double consumption;

    @Column(name = "tarefa")
    private String task;

    @Column(name = "status")
    private String status;

    @Column(name = "mes")
    private Long month;

    @Column(name = "ano")
    private Long year;

    @Column(name = "id_tarefa")
    private String taskId;

    @Column(name = "template")
    private String template;

}
