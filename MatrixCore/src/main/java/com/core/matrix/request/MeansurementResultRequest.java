/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.request;

import com.core.matrix.utils.ReportConstants;
import java.util.List;
import lombok.Data;

/**
 *
 * @author thiag
 */
@Data
public class MeansurementResultRequest {

    private List<Long> ids;
    private ReportConstants.ReportType type;
    private Long month;
    private Long year;

}
