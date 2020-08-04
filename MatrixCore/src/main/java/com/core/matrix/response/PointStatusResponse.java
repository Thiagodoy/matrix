/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.response;

import com.core.matrix.dto.PointStatusSummaryDTO;
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
public class PointStatusResponse {    
    private List<PointStatusSummaryDTO> summary;
    private Page page;
}
