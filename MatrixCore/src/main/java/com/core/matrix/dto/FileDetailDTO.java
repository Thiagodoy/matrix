/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.dto;

import javax.persistence.Column;
import lombok.Data;

/**
 *
 * @author thiag
 */
@Data
public class FileDetailDTO {

    public long line;
    public String fileName;
    public String agent;
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
    public String sourceCollection;
    public String notificationCollection;   
    public String quality;
    public String origem;
}
