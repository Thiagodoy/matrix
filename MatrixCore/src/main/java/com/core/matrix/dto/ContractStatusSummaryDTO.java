/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 *
 * @author thiag
 */
@Data
@AllArgsConstructor
public class ContractStatusSummaryDTO {
    private String status;
    private Long qtd;
}
