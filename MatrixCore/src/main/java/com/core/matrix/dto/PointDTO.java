/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.dto;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 *
 * @author thiag
 */
@Data
@AllArgsConstructor
public class PointDTO implements Serializable{   
    
    
    
    private String point;
    private String origem;
    private String message;       
}
