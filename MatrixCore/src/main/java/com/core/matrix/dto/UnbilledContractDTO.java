/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.dto;

import com.core.matrix.wbc.dto.ContractDTO;
import java.util.Optional;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *
 * @author thiag
 */
@Data
@EqualsAndHashCode(of = {"contractWbc"})
public class UnbilledContractDTO {
    
    private String name;
    private String nickname;
    private Long contractWbc;
    private Long contract;
    private Long contractRateio;
    private boolean isRateio;
    
    
    public UnbilledContractDTO(ContractDTO cdto){       
        
        this.name = cdto.getSNmEmpresaEpce();
        this.nickname = cdto.getSNmApelido();
        this.contractWbc = Long.valueOf(cdto.getSNrContrato());
        this.contract = cdto.getNCdContrato();
        this.contractRateio = cdto.getNCdContratoRateioControlador();
        this.isRateio = Optional.ofNullable(cdto.getBFlRateio()).isPresent() ? cdto.getBFlRateio().equals(1L) : false;
        
    }
    
}
