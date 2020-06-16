/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *
 * @author thiag
 */
@Data
@EqualsAndHashCode(of = {"processInstanceId", "taskId"})
public class ResultInLoteStatusDTO implements Serializable {

    private static final long serialVersionUID = -7547590126542275829L;

    private String processInstanceId;
    private String processInstanceName;
    private String taskId;
    private String taskName;
    private List<String> errors = new ArrayList<>();
    private String status;
    private String typeFile;
    
    
    public ResultInLoteStatusDTO(ProcessFilesInLoteStatusDTO ss){
        this.processInstanceId = ss.getProcessInstanceId();
        this.processInstanceName = ss.getProcessInstanceName();
        this.taskId = ss.getTaskId();
        this.taskName = ss.getTaskName();
        this.errors.addAll(ss.getErrors());
        this.status = ss.getStatus().toString();
        this.typeFile = ss.getTypeFile();
    }
    
}
