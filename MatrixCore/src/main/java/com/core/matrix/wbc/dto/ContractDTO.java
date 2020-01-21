/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.wbc.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 *
 * @author thiag
 */
@Data
//@AllArgsConstructor
public class ContractDTO {

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
    
    private Long bFlPublicado;
    
    private Long nCdSituacaoContrato;
    
    private String sDsSituacaoContrato;
    
    private String sDsObservacao;
    
    private Long nCdContratoPai;
    
    private String sNrReferencia;
    
    private Long nCdAgrupador;
    
    private String sDsAgrupador;

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
            Long bFlPublicado,
            Long nCdSituacaoContrato,
            String sDsSituacaoContrato,
            String sDsObservacao,
            Long nCdContratoPai,
            String sNrReferencia,
            Long nCdAgrupador,
            String sDsAgrupador
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
        this.bFlPublicado = bFlPublicado;
        this.nCdSituacaoContrato = nCdSituacaoContrato;
        this.sDsSituacaoContrato = sDsSituacaoContrato;
        this.sDsObservacao = sDsObservacao;
        this.nCdContratoPai = nCdContratoPai;
        this.sNrReferencia = sNrReferencia;
        this.nCdAgrupador = nCdAgrupador;
        this.sDsAgrupador = sDsAgrupador;

    }

}
