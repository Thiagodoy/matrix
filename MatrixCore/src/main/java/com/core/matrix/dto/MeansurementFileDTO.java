/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.dto;

import com.core.matrix.model.MeansurementFile;
import com.core.matrix.utils.Utils;
import lombok.Data;

/**
 *
 * @author thiag
 */
@Data
public class MeansurementFileDTO {    
    private String companyName;    
    private String processInstance;    
    private Long contract;
    private String point;
    private String status;
    
    
    public MeansurementFileDTO(MeansurementFile file){
        this.companyName = file.getNickname();
        this.processInstance = file.getProcessInstanceId();
        this.contract = file.getWbcContract();
        this.point = file.getMeansurementPoint(); 
        this.status = Utils.getStatus(file.getStatus());
    }
}
