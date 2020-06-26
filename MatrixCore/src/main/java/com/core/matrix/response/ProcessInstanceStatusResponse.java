/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.response;

import lombok.Data;

/**
 *
 * @author thiag
 */

@Data
public class ProcessInstanceStatusResponse {   
    
    private String id;
    private String processName;
    private String Status;
    
}
