/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.resource;

import com.core.matrix.response.UserMetricResponse;
import com.core.matrix.service.UserMetricsService;
import static com.core.matrix.utils.Url.URL_API_USER_METRICS;
import java.security.Principal;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author thiag
 */
@RestController
@RequestMapping(value = URL_API_USER_METRICS)
public class UserMetricsResource {

    @Autowired
    private UserMetricsService service;

    @RequestMapping(value = "/closedTask", method = RequestMethod.GET)
    public ResponseEntity closedTask(@RequestParam(name = "type") String type, Principal principal) {
        try {

            UserMetricsService.MetricType metricType = UserMetricsService.MetricType.valueOf(type);
            UserMetricResponse response = this.service.metricTaskClosed(metricType, principal.getName());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Logger.getLogger(UserResource.class.getName()).log(Level.SEVERE, "[closedTask]", e);
            return ResponseEntity.status(HttpStatus.resolve(500)).build();
        }
    }

    @RequestMapping(value = "/userOpenClosedTasksMetrics", method = RequestMethod.GET)
    public ResponseEntity userOpenClosedTasksMetrics(@RequestParam(name = "type") String type, Principal principal) {
        try {

            UserMetricsService.MetricType metricType = UserMetricsService.MetricType.valueOf(type);
            UserMetricResponse response = this.service.userOpenClosedTasksMetrics(metricType, principal.getName());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Logger.getLogger(UserResource.class.getName()).log(Level.SEVERE, "[userOpenClosedTasksMetrics]", e);
            return ResponseEntity.status(HttpStatus.resolve(500)).build();
        }
    }

    @RequestMapping(value = "/userTimeOnTaskMetric", method = RequestMethod.GET)
    public ResponseEntity userTimeOnTaskMetric(@RequestParam(name = "type") String type, Principal principal) {
        try {

            UserMetricsService.MetricType metricType = UserMetricsService.MetricType.valueOf(type);
            UserMetricResponse response = this.service.userTimeOnTaskMetric(metricType, principal.getName());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Logger.getLogger(UserResource.class.getName()).log(Level.SEVERE, "[userTimeOnTaskMetric]", e);
            return ResponseEntity.status(HttpStatus.resolve(500)).build();
        }
    }

}
