/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.dto;

import com.core.matrix.utils.MeansurementFileStatus;
import com.core.matrix.utils.Utils;
import lombok.Data;

/**
 *
 * @author thiag
 */
@Data
public class FileStatusDTO {

    private Long count;
    private String status;

    public FileStatusDTO(Long count, String status) {
        this.count = count;
        this.setStatus(status);
    }

    private void setStatus(String name) {
        this.status = Utils.getStatus(name);
    }

}
