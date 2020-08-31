/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.dto;

import com.core.matrix.model.Product;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 *
 * @author thiag
 */
@Data
@EqualsAndHashCode(of = {"code", "description"})
@NoArgsConstructor
public class SubMarketDTO {

    private Long code;
    private String description;   
    
    public SubMarketDTO(Product product) {
        this.code = product.getSubMarket();
        this.description = product.getSubMarketDescription();
    }

}
