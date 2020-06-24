/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author thiag
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Action {

    public enum ActionType {
        REMOVE
    }    
    
    private String taskId;
    private Long notificationId;
    private ActionType type;
}
