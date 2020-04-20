/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author thiag
 */
@Entity
@Table(schema = "matrix", name = "monitoramento_view")
@Data
@JsonIgnoreProperties(value = {"id"})
public class Monitoring implements Serializable {

    private static final long serialVersionUID = 359460498870162995L;

    @Id
    @Column(name = "ID")
    private Long id;
    
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
