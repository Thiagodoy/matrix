/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.workflow.service;

import com.core.matrix.response.ProcessDefinitionResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.image.ProcessDiagramGenerator;
import org.activiti.image.impl.DefaultProcessDiagramGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author thiag
 */
@Service
public class RepositoryActivitiService {

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runtimeService;

    public Deployment getDeployment(String deploymentId) {
        return this.repositoryService.createDeploymentQuery().deploymentId(deploymentId).singleResult();
    }

    public void upload(String fileName, InputStream content) throws IOException {
        this.repositoryService.createDeployment().addInputStream(fileName, content).name(fileName).deploy();
        content.close();
    }

    public void activateProcessDefinition(String processDefinitionId) {
        this.repositoryService.activateProcessDefinitionById(processDefinitionId);
    }

    public void suspendProcessDefinition(String processDefinitionId) {
        this.repositoryService.suspendProcessDefinitionById(processDefinitionId);
    }

    public void deleteProcessDefinition(String processDefinitionId) {
        this.repositoryService.deleteDeployment(processDefinitionId, true);
    }

    public List<ProcessDefinitionResponse> listAll() {
        return this.repositoryService
                .createProcessDefinitionQuery()
                .latestVersion()
                .list()
                .parallelStream()
                .map(p -> new ProcessDefinitionResponse(p))
                .collect(Collectors.toList());

    }

    public InputStream generateProcessDiagram(String processDefinitionId, String processInstanceId) {

        if (processDefinitionId != null && processInstanceId == null) {

            ProcessDefinition processDefinition = this.repositoryService
                    .createProcessDefinitionQuery()
                    .processDefinitionId(processDefinitionId)
                    .singleResult();
            
            String diagramResourceName = processDefinition.getDiagramResourceName();
            
            return this.repositoryService.getResourceAsStream(processDefinition.getDeploymentId(), diagramResourceName);

        } else {

            ProcessDiagramGenerator pdg = new DefaultProcessDiagramGenerator();
            ProcessDefinitionEntity processDefinition = (ProcessDefinitionEntity) ((RepositoryServiceImpl) this.repositoryService).getDeployedProcessDefinition(processDefinitionId);
            BpmnModel bpmnModel = this.repositoryService.getBpmnModel(processDefinitionId);

            if (processDefinition != null && processDefinition.isGraphicalNotationDefined() && processInstanceId != null) {
                return pdg.generateDiagram(bpmnModel, "jpeg", this.runtimeService.getActiveActivityIds(processInstanceId));
            } else {
                return null;
            }

        }

    }

}
