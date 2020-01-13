/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.response;

import com.core.matrix.utils.Utils;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.Data;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.task.DelegationState;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskInfo;

/**
 *
 * @author thiag
 */
@Data
public class TaskResponse {

    private String name;
    private String description;
    private String taskId;
    private String template;
    private String processInstanceId;
    private String processDefinitionName;
    private String processDefinitionId;
    private String createTime;
    private Map<String, Object> globalVariables;
    private Map<String, Object> localVariables;
    private String delegationState;
    private String assigne;
    private String delegate;
    private String finalized;
    private List<String> breadCrump;

    public TaskResponse(TaskInfo task) {

        this.name = task.getName();
        this.description = task.getDescription();
        this.taskId = task.getId();
        this.template = task.getFormKey();
        this.processDefinitionId = task.getProcessDefinitionId();
        this.processInstanceId = task.getProcessInstanceId();
        this.createTime = Utils.dateToString(task.getCreateTime());
        this.globalVariables = task.getProcessVariables();
        this.localVariables = task.getTaskLocalVariables();
        
        if(task instanceof Task){
            Task t = (Task) task;
            Optional<DelegationState> opt = Optional.ofNullable(t.getDelegationState()); 
            this.delegationState = opt.isPresent() ? t.getDelegationState().toString() : "" ;
            
            if(opt.isPresent() && opt.get().equals(DelegationState.PENDING)){
               this.assigne = t.getOwner();
               this.delegate = Optional.ofNullable(task.getAssignee()).isPresent() ? task.getAssignee() : "";
            }else{
                this.assigne = Optional.ofNullable(task.getAssignee()).isPresent() ? task.getAssignee() : "";
                this.delegate = "";
            }
        }else{
            HistoricTaskInstance ht = (HistoricTaskInstance) task;
            this.assigne = Optional.ofNullable(ht.getAssignee()).isPresent() ? ht.getAssignee() : "";
            this.finalized = String.valueOf(Optional.ofNullable(ht.getEndTime()).isPresent());
        }

    }

}
