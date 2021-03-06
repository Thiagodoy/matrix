/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.dto;

import com.core.matrix.wbc.dto.ContractWbcInformationDTO;
import com.core.matrix.wbc.dto.CompanyDTO;
import java.io.Serializable;
import lombok.Data;

/**
 *
 * @author thiag
 */
@Data
public class ConsumptionResult implements Serializable {

    private static final long serialVersionUID = -6502996779067937478L;

    private String meansurementPoint;
    private Double result = 0D;
    private String error;
    private Long contractId;
    private Double factorAtt;
    private Double percentLoss;
    private Double proinfa;
    private CompanyDTO empresa;
    private ContractWbcInformationDTO information;
    private Double SolicitadoLiquido;

}
