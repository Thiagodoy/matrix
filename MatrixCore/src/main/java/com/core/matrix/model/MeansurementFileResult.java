/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.model;

import com.core.matrix.dto.MeansurementFileResultStatusDTO;
import com.core.matrix.wbc.dto.ContractWbcInformationDTO;
import com.google.common.base.Optional;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedNativeQuery;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author thiag
 */
@SqlResultSetMapping(name = "statusBillingDTO",
        classes = @ConstructorResult(
                targetClass = MeansurementFileResultStatusDTO.class,
                columns = {
                    @ColumnResult(name = "id_arquivo_de_medicao", type = Long.class)
                    ,
                    @ColumnResult(name = "ano", type = Long.class)
                    ,
                    @ColumnResult(name = "mes", type = Long.class)
                    ,
                    @ColumnResult(name = "wbc_contrato", type = Long.class)
                    ,
                    @ColumnResult(name = "wbc_ponto_de_medicao", type = String.class)
                    ,
                    @ColumnResult(name = "montante_liquido", type = Double.class)
                    ,
                    @ColumnResult(name = "status", type = String.class)
                    ,
                    @ColumnResult(name = "data_criacao", type = Date.class)
                }))

@NamedNativeQuery(name = "MeansurementFileResult.getStatusBilling",
        query = "SELECT \n"
        + "    a.id_arquivo_de_medicao,\n"
        + "    a.ano,\n"
        + "    a.mes,\n"
        + "    case b.contrato_pai when 1 then c.wbc_contrato else a.wbc_contrato end as wbc_contrato,\n"
        + "    a.wbc_ponto_de_medicao,\n"
        + " case b.justificativa when 'APROVADO' then b.montante_liquido_ajustado else b.montante_liquido end as montante_liquido,\n"
        + "    a.status,\n"
        + "    a.data_criacao\n"
        + "FROM\n"
        + "    mtx_arquivo_de_medicao a\n"
        + "        INNER JOIN\n"
        + "    mtx_arquivo_de_medicao_resultado b ON a.id_arquivo_de_medicao = b.id_arquivo_de_medicao\n"
        + "        LEFT JOIN\n"        
        + "    mtx_contrato_informacao_complementar c ON  b.wbc_contrato = c.wbc_codigo_contrato or b.wbc_contrato = c.wbc_contrato\n"
        + "\n"        
        + "WHERE\n"
        + "    a.status IN ('APPROVED')\n"
        + "        AND b.act_id_process not in (select distinct mr.act_id_processo from mtx_aqruivo_de_medicao_recompra mr) \n"
        + "        AND a.ano = :year\n"
        + "        AND a.mes = :month\n"
        + "    order by a.data_criacao",
        resultSetMapping = "statusBillingDTO")

@Entity
@Table(name = "mtx_arquivo_de_medicao_resultado")
@Data
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
    
    public MeansurementFileResult(ContractWbcInformationDTO informationDTO, String idProcess) {

        this.qtdHiredMin = informationDTO.getQtdHiredMin();
        this.qtdHiredMax = informationDTO.getQtdHiredMax();
        this.qtdHired = informationDTO.getQtdHired();
        this.idProcess = idProcess;
        this.limitMin = informationDTO.getLimitMin();
        this.limitMax = informationDTO.getLimitMax();
        this.price = informationDTO.getPrice();

    }
    
    
    public void update(MeansurementFileResult result){
        
        if(Optional.fromNullable(result.getAmountLiquidoAdjusted()).isPresent() && !result.getAmountLiquidoAdjusted().equals(this.amountLiquidoAdjusted)){
            this.amountLiquidoAdjusted = result.getAmountLiquidoAdjusted();
        }
        
        if(Optional.fromNullable(result.getJustify()).isPresent() && !result.getJustify().equals(this.justify)){
            this.justify = result.getJustify();
        }
        
    }

}
