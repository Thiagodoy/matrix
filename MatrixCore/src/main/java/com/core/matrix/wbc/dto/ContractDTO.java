/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.wbc.dto;

import java.time.LocalDateTime;
import lombok.Data;

/**
 *
 * @author thiag
 */
@Data
public class ContractDTO {

    private Long nCdContrato;
    private String sNrContrato;
    private LocalDateTime tDdInicio;
    private LocalDateTime tDdTermino;
    private Long nCdSubmercado;
    private String sDsSubmercado;
    private Long nCdTipo;
    private String sDsTipo;
    private Long nCdEmpresaContratada;
    private String sNmEmpresaContratada;
    

//    [nCdContrato]
//,CT.[sNrContrato]
//,CT.[sNmContrato]
//,CT.[tDdInicio]
//,CT.[tDdTermino]
//,CT.[nCdSubmercado]
//,SM.[sDsSubmercado]
//,CT.[nCdTipo]
//,TP.[sDsTipo]
//,CT.[nCdEmpresaContratada]
//,EPCA.[sNmEmpresa]
//,CT.[nCdEmpresaContratante]
//,EPCE.[sNmEmpresa]
//,CT.[bFlPublicado]
//,CT.[nCdSituacaoContrato]
//,SC.sDsSituacaoContrato
//,CT.[sDsObservacao]
//,CT.[nCdContratoPai]
//,CT.[sNrReferencia]
//,CT.[nCdAgrupador]
//,CA.sDsAgrupador
//    
}
