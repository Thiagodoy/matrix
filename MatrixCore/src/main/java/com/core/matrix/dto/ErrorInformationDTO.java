/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.dto;

import com.core.matrix.wbc.dto.EmpresaDTO;
import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 *
 * @author thiag
 */
@Data
@AllArgsConstructor
public class ErrorInformationDTO<T> implements Serializable{

    private String information;
    private List<T> errors;
    private EmpresaDTO company;

}
