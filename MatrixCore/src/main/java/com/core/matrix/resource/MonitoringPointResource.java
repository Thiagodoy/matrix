/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.resource;

import com.core.matrix.dto.PointStatusSummaryDTO;
import com.core.matrix.model.MeansurementPointStatus;
import com.core.matrix.response.PointStatusResponse;
import com.core.matrix.service.MeansurementPointStatusService;
import com.core.matrix.specifications.MeansurementPointStatusSpecification;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import static com.core.matrix.utils.Url.URL_API_MONITORING_POINT;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author thiag
 */
@RestController
@RequestMapping(value = URL_API_MONITORING_POINT)
public class MonitoringPointResource {
    
    @Autowired
    private MeansurementPointStatusService pointStatusService;
    
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity status(
            @RequestParam(name = "month") long month,
            @RequestParam(name = "year") long year,
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "point", required = false) String point,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        try {
            
            Specification<MeansurementPointStatus> spc = MeansurementPointStatusSpecification.find(status, point, month, year);
            PageRequest pageRequest = PageRequest.of(page, size, Sort.by("point"));            
            Page pageResponse = this.pointStatusService.find(spc, pageRequest);
            List<PointStatusSummaryDTO> summary = this.pointStatusService.summary(month, year);            
            PointStatusResponse response = new PointStatusResponse(summary, pageResponse);            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Logger.getLogger(MonitoringPointResource.class.getName()).log(Level.SEVERE, "[status]", e);
            return ResponseEntity.status(HttpStatus.resolve(500)).body(e.getMessage());
        }
    }
    
}
