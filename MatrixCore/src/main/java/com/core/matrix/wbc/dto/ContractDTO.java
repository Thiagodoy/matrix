/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.wbc.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author thiag
 */
@Data
//@AllArgsConstructor
@JsonIgnoreProperties(value = {"tddTermino","tddInicio"})
@EqualsAndHashCode(of = {"sNrContrato"})
public class ContractDTO implements Serializable{

    private static final long serialVersionUID = 3749064926674036404L;

    private Long nCdContrato;

    private String sNrContrato;

    private String sNmContrato;

    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING)
    private LocalDateTime tDdInicio;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING)
    private LocalDateTime tDdTermino;

    private Long nCdSubmercado;

    private String sDsSubmercado;

    private Long nCdTipo;

    private String sDsTipo;

    private Long nCdEmpresaContratada;

    private String sNmEmpresaEpca;

    private Long nCdEmpresaContratante;

    private String sNmEmpresaEpce;
   
    private String sNmFantasia;
        
    private String sNmApelido;

    private Long bFlPublicado;

    private Long nCdSituacaoContrato;

    private String sDsSituacaoContrato;

    private String sDsObservacao;

    private Long nCdContratoPai;

    private String sNrReferencia;

    private Long nCdAgrupador;

    private String sDsAgrupador;

    private Long bFlRateio;

    private Long nCdContratoRateioControlador;
    
    private String meansurementPoint;
    
    private Long nCdSiglaCCEEContratante;
    
    private Long nCdPerfilCCEE;
    
    private String sNrCnpj;

    public ContractDTO(
            Long nCdContrato,
            String sNrContrato,
            String sNmContrato,
            String sNrReferencia,
            LocalDateTime tDdInicio,
            LocalDateTime tDdTermino,
            Long nCdSubmercado,
            String sDsSubmercado,
            Long nCdTipo,
            String sDsTipo,
            Long nCdEmpresaContratada,
            String sNmEmpresaEpca,
            Long nCdEmpresaContratante,
            String sNmEmpresaEpce,
            String sNmFantasia,
            String sNmApelido,
            Long bFlPublicado,
            Long nCdSituacaoContrato,
            String sDsSituacaoContrato,
            String sDsObservacao,
            Long nCdContratoPai,
            Long nCdAgrupador,
            String sDsAgrupador,
            Long bFlRateio,
            Long nCdContratoRateioControlador,
            Long nCdSiglaCCEEContratante,
            Long nCdPerfilCCEE,
            String sNrCnpj
    ) {

        this.nCdContrato = nCdContrato;
        this.sNrContrato = sNrContrato;
        this.sNmContrato = sNmContrato;
        this.tDdInicio = tDdInicio;
        this.tDdTermino = tDdTermino;
        this.nCdSubmercado = nCdSubmercado;
        this.sDsSubmercado = sDsSubmercado;
        this.nCdTipo = nCdTipo;
        this.sDsTipo = sDsTipo;
        this.nCdEmpresaContratada = nCdEmpresaContratada;
        this.sNmEmpresaEpca = sNmEmpresaEpca;
        this.nCdEmpresaContratante = nCdEmpresaContratante;
        this.sNmEmpresaEpce = sNmEmpresaEpce;
        this.sNmFantasia = sNmFantasia;
        this.sNmApelido = sNmApelido;
        this.bFlPublicado = bFlPublicado;
        this.nCdSituacaoContrato = nCdSituacaoContrato;
        this.sDsSituacaoContrato = sDsSituacaoContrato;
        this.sDsObservacao = sDsObservacao;
        this.nCdContratoPai = nCdContratoPai;
        this.sNrReferencia = sNrReferencia;
        this.nCdAgrupador = nCdAgrupador;
        this.sDsAgrupador = sDsAgrupador;
        this.bFlRateio = bFlRateio;
        this.nCdContratoRateioControlador = nCdContratoRateioControlador;
        this.nCdSiglaCCEEContratante = nCdSiglaCCEEContratante;
        this.nCdPerfilCCEE = nCdPerfilCCEE;
        this.sNrCnpj = sNrCnpj;
    }

    public ContractDTO(
            Long nCdContrato,
            String sNrContrato,
            String sNmContrato,
            String sNrReferencia,
            LocalDateTime tDdInicio,
            LocalDateTime tDdTermino,
            Long nCdEmpresaContratada,
            String sNmEmpresaEpca,
            Long nCdEmpresaContratante,
            String sNmEmpresaEpce,
            String sNmFantasia,
            String sNmApelido,     
            Long nCdSituacaoContrato,
            String sDsSituacaoContrato,
            Long bFlRateio,
            Long nCdContratoRateioControlador,
            Long nCdSiglaCCEEContratante,
            Long nCdPerfilCCEE,
            String sNrCnpj
    ) {

        this.nCdContrato = nCdContrato;
        this.sNrContrato = sNrContrato;
        this.sNmContrato = sNmContrato;
        this.tDdInicio = tDdInicio;
        this.tDdTermino = tDdTermino;
        this.nCdEmpresaContratada = nCdEmpresaContratada;
        this.sNmEmpresaEpca = sNmEmpresaEpca;
        this.nCdEmpresaContratante = nCdEmpresaContratante;
        this.sNmEmpresaEpce = sNmEmpresaEpce;
        this.sNmApelido = sNmApelido;
        this.sNmEmpresaEpce = sNmEmpresaEpce;
        this.nCdSituacaoContrato = nCdSituacaoContrato;
        this.sDsSituacaoContrato = sDsSituacaoContrato;
        this.sNrReferencia = sNrReferencia;
        this.bFlRateio = bFlRateio;
        this.nCdContratoRateioControlador = nCdContratoRateioControlador;
        this.nCdSiglaCCEEContratante = nCdSiglaCCEEContratante;
        this.nCdPerfilCCEE = nCdPerfilCCEE;  
        this.sNrCnpj = sNrCnpj;
    }

    public ContractDTO(
            Long nCdContrato,
            String sNrContrato,
            String sNmContrato,
            LocalDateTime tDdInicio,
            LocalDateTime tDdTermino,
            Long nCdSubmercado,
            String sDsSubmercado,
            Long nCdTipo,
            String sDsTipo,
            Long nCdEmpresaContratada,
            String sNmEmpresaEpca,
            Long nCdEmpresaContratante,
            String sNmEmpresaEpce,
            String sNmFantasia,
            String sNmApelido,
            Long bFlPublicado,
            Long nCdSituacaoContrato,
            String sDsSituacaoContrato,
            String sDsObservacao,
            Long nCdContratoPai,
            String sNrReferencia,
            Long nCdAgrupador,
            String sDsAgrupador,
            Long nCdSiglaCCEEContratante,
            Long nCdPerfilCCEE,
            String sNrCnpj
    ) {

        this.nCdContrato = nCdContrato;
        this.sNrContrato = sNrContrato;
        this.sNmContrato = sNmContrato;
        this.tDdInicio = tDdInicio;
        this.tDdTermino = tDdTermino;
        this.nCdSubmercado = nCdSubmercado;
        this.sDsSubmercado = sDsSubmercado;
        this.nCdTipo = nCdTipo;
        this.sDsTipo = sDsTipo;
        this.nCdEmpresaContratada = nCdEmpresaContratada;
        this.sNmEmpresaEpca = sNmEmpresaEpca;
        this.nCdEmpresaContratante = nCdEmpresaContratante;
        this.sNmEmpresaEpce = sNmEmpresaEpce;
        this.sNmFantasia = sNmFantasia;
        this.sNmApelido = sNmApelido;
        this.bFlPublicado = bFlPublicado;
        this.nCdSituacaoContrato = nCdSituacaoContrato;
        this.sDsSituacaoContrato = sDsSituacaoContrato;
        this.sDsObservacao = sDsObservacao;
        this.nCdContratoPai = nCdContratoPai;
        this.sNrReferencia = sNrReferencia;
        this.nCdAgrupador = nCdAgrupador;
        this.sDsAgrupador = sDsAgrupador;
        this.nCdSiglaCCEEContratante = nCdSiglaCCEEContratante;
        this.nCdPerfilCCEE = nCdPerfilCCEE;
        this.sNrCnpj = sNrCnpj;
    }
    
    @Override
    public String toString(){
        
        String epca = this.sNmEmpresaEpca != null && this.sNmEmpresaEpca.length() > 32 ?  this.sNmEmpresaEpca.substring(0, 32) : this.sNmEmpresaEpca ; 
        String epce = this.sNmEmpresaEpce != null && this.sNmEmpresaEpce.length() > 31 ?  this.sNmEmpresaEpce.substring(0, 31) : this.sNmEmpresaEpce ; 
        
        
        return  MessageFormat.format("##########################################################\n"
                +                    "# Contrato : {0}#\n"
                +                    "# Empresa Contratada : {1}#\n"
                +                    "# Empresa Contratante : {2}#\n"
                +                    "##########################################################\n", 
                StringUtils.rightPad(this.sNrContrato, 44, null) ,
                StringUtils.rightPad(epca,34,null),
                StringUtils.rightPad(epce,33,null));
    }

}
