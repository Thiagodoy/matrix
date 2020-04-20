/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.wbc.dto;

import com.core.matrix.wbc.model.Produto;
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
public class ProdutoDTO implements Serializable {

    private static final long serialVersionUID = -8716578903394081946L;   
    

    private Integer nCdPerfilCCEE;
    private String sDsSiglaCCEE;
    private String sDsPerfilCCEE;
    

    public ProdutoDTO(Produto produto) {
        this.nCdPerfilCCEE = produto.getNCdPerfilCCEE();
        this.sDsSiglaCCEE = produto.getSDsSiglaCCEE();
        this.sDsPerfilCCEE = produto.getSDsPerfilCCEE();
    }

}
