/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.response;

import com.core.matrix.dto.ContractPointDTO;
import com.core.matrix.model.ContractMtx;
import java.util.List;
import lombok.Data;

/**
 *
 * @author thiag
 */
@Data
public class ContractMtxResponse {    
    private List<ContractMtx> contracts;
    private List<ContractPointDTO> associations;    
}
