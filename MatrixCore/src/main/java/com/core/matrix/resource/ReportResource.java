/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.resource;

import com.core.matrix.request.MeansurementResultRequest;
import com.core.matrix.service.ReportService;
import javax.servlet.http.HttpServletResponse;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author thiag
 */
@RestController
@RequestMapping(value = "/api/report")
public class ReportResource {

    @Autowired
    private ReportService reportService;

    @RequestMapping(value = "/meansurement-result", method = RequestMethod.POST)
    public ResponseEntity generate(@RequestBody MeansurementResultRequest request, HttpServletResponse response) {
        try {
            reportService.export(response, request);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            Logger.getLogger(ReportResource.class.getName()).log(Logger.Level.FATAL, "[generate]", e);
            return ResponseEntity.status(HttpStatus.resolve(500)).body(e.getMessage());
        }
    }
    
    
    @RequestMapping(value = "/monitoring", method = RequestMethod.POST)
    public ResponseEntity generate(@RequestParam(required = false, name = "status") String status,
            @RequestParam(required = false, name = "instanciaDoProcesso") String instanciaDoProcesso,
            @RequestParam(required = false, name = "wbcContrato") String wbcContrato, 
            @RequestParam(required = false, name = "pontoMedicao") String pontoMedicao,
            @RequestParam(required = false, name = "empresa") String empresa,
            @RequestParam(required = true, name = "ano") String ano,
            @RequestParam(required = true, name = "mes") String mes,
            @RequestParam(name = "page", defaultValue = "0")int page,
            @RequestParam(name = "size", defaultValue = "10")int size,
            HttpServletResponse response) {
        try {
            //reportService.export(response, request);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            Logger.getLogger(ReportResource.class.getName()).log(Logger.Level.FATAL, "[generate]", e);
            return ResponseEntity.status(HttpStatus.resolve(500)).body(e.getMessage());
        }
    }

}
