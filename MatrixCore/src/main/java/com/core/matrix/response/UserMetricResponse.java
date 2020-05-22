/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.response;

import com.core.matrix.service.UserMetricsService;
import java.util.Map;
import lombok.Data;

/**
 *
 * @author thiag
 */
@Data
public class UserMetricResponse<T> {

    private String start;
    private String end;
    private Map<String, T> data;
    private UserMetricsService.MetricType type;

    
    
    
    
    
}
