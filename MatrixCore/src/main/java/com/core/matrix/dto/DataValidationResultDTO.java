/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.dto;

import java.io.Serializable;
import lombok.Data;

/**
 *
 * @author thiag
 */
@Data
public class DataValidationResultDTO implements Serializable{

    private static final long serialVersionUID = -2147728596557657586L;    
    
    private Long idFile;
    private String fileName;
    private String point;
    private Double totalScde;
    private Long hours;
    private Long days;
    private Double inputManual;
    
    
}
