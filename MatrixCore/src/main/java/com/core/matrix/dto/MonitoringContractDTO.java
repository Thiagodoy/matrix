/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author thiag
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MonitoringContractDTO {   
    
    private Long contract;
    private String companyName;
    private String taskId;
    private String template;
    private String taskName;
    private Long hours;
    private Double comsumption;
    private Long rateio;
    private String status;
    
}
