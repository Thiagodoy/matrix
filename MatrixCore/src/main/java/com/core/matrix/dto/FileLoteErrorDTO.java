/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.Data;

/**
 *
 * @author thiag
 */
@Data
public class FileLoteErrorDTO implements Serializable {

    private static final long serialVersionUID = 8318640230449031062L;

    private String fileName;
    private List<String> errors;
    
    public void setError(String message){
        if(!Optional.ofNullable(this.errors).isPresent()){
            this.errors = new ArrayList<>();
        }
        
        this.errors.add(message);
    }
    

}
