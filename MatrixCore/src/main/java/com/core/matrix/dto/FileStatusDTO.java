/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.dto;

import java.io.Serializable;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author thiag
 */
@Data
@NoArgsConstructor
public class FileStatusDTO implements Serializable {

    private static final long serialVersionUID = -711844277177553695L;

    private Long count;
    private String status;

    public FileStatusDTO(Long count, String status) {
        this.count = count;
        this.status = status;
    }
}
