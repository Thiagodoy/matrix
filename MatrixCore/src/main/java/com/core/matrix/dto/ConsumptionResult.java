/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.dto;

import lombok.Data;

/**
 *
 * @author thiag
 */
@Data
public class ConsumptionResult {
    
    private String meansurementPoint;
    private Double result = 0D;
    private String error;    
    
}
