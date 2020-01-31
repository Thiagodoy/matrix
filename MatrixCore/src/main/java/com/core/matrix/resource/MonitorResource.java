/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.resource;

import com.core.matrix.dto.MeansurementFileStatusDTO;
import com.core.matrix.service.MeansurementFileService;
import com.core.matrix.wbc.service.ContractService;
import java.util.List;
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
@RequestMapping(value = "/api/monitor")
public class MonitorResource {

    @Autowired
    private MeansurementFileService meansurementFileService;
    
    @Autowired
    private ContractService contractService;
    
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity get(@RequestParam(name = "year", required = true) Long year, @RequestParam(name = "month", required = true) Long month) {

        try {
            
            List<MeansurementFileStatusDTO> response = meansurementFileService.getStatus(year.intValue(), month.intValue());
            Long countContract = contractService.countContract();
            MeansurementFileStatusDTO file = new MeansurementFileStatusDTO("CONTRACT", countContract);
            response.add(file);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Logger.getLogger(MonitorResource.class.getName()).log(Level.SEVERE, "[get]", e);
            return ResponseEntity.status(HttpStatus.resolve(500)).body(e.getMessage());
        }
    }

}
