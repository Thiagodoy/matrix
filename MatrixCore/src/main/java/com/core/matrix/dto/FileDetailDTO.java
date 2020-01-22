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
public class FileDetailDTO {

    
    public String meansurementPoint;
    public String date;
    public String hour;
    public String energyType;
    public String generationActive;
    public String consumptionActive;
    public String generationReactivate;
    public String consumptionReactivate;
    public String range;
    public String situation;
    public String reason;
}
