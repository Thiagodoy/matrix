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

    private String taskName;
    private Long contract;
    private String point;
    private String nickname;
    private String status;
    private String processInstanceId;

    public MeansurementFileDTO(String taskName, Long contract, String point, String nickname, String status, String processInstance) {
        this.taskName = taskName;
        this.contract = contract;
        this.point = point;
        this.nickname = nickname;
        this.status = status;
        this.processInstanceId = processInstance;
    }

    public MeansurementFileDTO(MeansurementFile file) {
        this.contract = file.getWbcContract();
        this.processInstanceId = file.getProcessInstanceId();
        this.point = file.getMeansurementPoint();
        this.status = Utils.getStatus(file.getStatus());
        this.nickname = file.getNickname();
    }

}
