/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.dto;

import java.io.Serializable;
import java.util.Map;
import lombok.Data;

/**
 *
 * @author thiag
 */
@Data
public class LoteDTO<T,S> implements Serializable{   

    private static final long serialVersionUID = 2703685355017098424L;
    private Map<T,S> lotes;    
}
