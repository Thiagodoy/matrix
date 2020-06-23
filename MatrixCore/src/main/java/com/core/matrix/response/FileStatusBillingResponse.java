/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.response;

import com.core.matrix.dto.FileStatusDTO;
import com.core.matrix.model.MeansurementFileDTO;
import java.io.Serializable;
import java.util.List;
import lombok.Data;

/**
 *
 * @author thiag
 */
@Data
public class FileStatusBillingResponse implements Serializable {

    private static final long serialVersionUID = -1929956763395654015L;
    private List<FileStatusDTO> fileStatusDTOs;
    private PageResponse<MeansurementFileDTO> page;

}
