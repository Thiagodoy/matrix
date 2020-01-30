/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.dto;

import javax.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 *
 * @author thiag
 */
@Data
@AllArgsConstructor
public class ContractInformationDTO {

    private Long contractId;
    private String pointId;
    private Double percentOfLoss;
    private Double factorAttendanceCharge;
    private Double proinfa;

}
