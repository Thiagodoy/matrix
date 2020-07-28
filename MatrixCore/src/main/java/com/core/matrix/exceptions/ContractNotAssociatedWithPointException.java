/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.exceptions;

import lombok.Data;

/**
 *
 * @author thiag
 */
@Data
public class ContractNotAssociatedWithPointException extends Exception{
    
    
    public ContractNotAssociatedWithPointException(){
        super("Contract isn't associated with a point");
    } 
    
}
