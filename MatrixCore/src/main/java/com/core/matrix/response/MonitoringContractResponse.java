/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.response;

import com.core.matrix.dto.MonitoringContractDTO;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 *
 * @author thiag
 */
@Data
@AllArgsConstructor
public class MonitoringContractResponse {    
    private List<MonitoringContractDTO> list;
    private Map<String, Long> summary;    
}
