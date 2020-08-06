/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.response;

import com.core.matrix.dto.ContractStatusSummaryDTO;
import com.core.matrix.model.ContractMtxStatus;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

/**
 *
 * @author thiag
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContractStatusResponse {
    private List<ContractStatusSummaryDTO> summary;
    private Page<ContractMtxStatus> page;
}
