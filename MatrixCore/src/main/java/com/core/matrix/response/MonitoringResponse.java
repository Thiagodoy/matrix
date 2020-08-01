/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.response;

import com.core.matrix.dto.MonitoringFilterDTO;
import com.core.matrix.dto.MonitoringStatusDTO;
import com.core.matrix.model.Monitoring;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.domain.Page;

/**
 *
 * @author thiag
 */
@Data
@AllArgsConstructor
public class MonitoringResponse {

    private Page<Monitoring> data;
    private List<MonitoringStatusDTO> status;
    private List<MonitoringFilterDTO> filters;
}
