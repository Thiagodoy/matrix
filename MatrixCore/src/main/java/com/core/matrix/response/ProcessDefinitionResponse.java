/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.response;

import lombok.Data;
import org.activiti.engine.repository.ProcessDefinition;

/**
 *
 * @author thiag
 */
@Data
public class ProcessDefinitionResponse {

    private String id;
    private String key;
    private String name;
    private String resourceName;
    private String description;
    private String version;
    private String isSuspended;
    private String status;
    private String deploymentId;
    
    public ProcessDefinitionResponse(ProcessDefinition entity) {
        this.id = entity.getId();
        this.key = entity.getKey();
        this.name = entity.getName();
        this.resourceName = entity.getResourceName();
        this.description = entity.getDescription();
        this.version = String.valueOf(entity.getVersion());
        this.isSuspended = String.valueOf(entity.isSuspended());
        this.status = entity.isSuspended() ? "Suspenso" : "Ativo";
        this.deploymentId = entity.getDeploymentId();
    }
}
