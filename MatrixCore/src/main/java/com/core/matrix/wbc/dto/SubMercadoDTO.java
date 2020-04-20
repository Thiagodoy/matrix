/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.wbc.dto;

import com.core.matrix.wbc.model.SubMercado;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author thiag
 */
@Data
@AllArgsConstructor()
@NoArgsConstructor
public class SubMercadoDTO implements Serializable {

    private static final long serialVersionUID = 3772922880793570356L;

    private Integer nCdSubmercado;
    private String sDsSubmercado;

    public SubMercadoDTO(SubMercado subMercado) {
        this.nCdSubmercado = subMercado.getNCdEmpresa();
        this.sDsSubmercado = subMercado.getSDsSubmercado();
    }

}
