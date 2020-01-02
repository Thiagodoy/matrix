/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.workflow.service;

import java.util.List;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.ProcessDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author thiag
 */
@Service
public class WorkflowService {

    @Autowired
    private ProcessEngine processEngine;

    public List<ProcessDefinition> getProcessDefinitions(String userId) {

        RepositoryService repositoryService = processEngine.getRepositoryService();
        return repositoryService.createProcessDefinitionQuery().active().latestVersion().startableByUser(userId).list();
    }

}
