/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.resource;

import com.core.matrix.dto.MonitoringFilterDTO;
import com.core.matrix.dto.MonitoringStatusDTO;
import com.core.matrix.model.Monitoring;
import com.core.matrix.repository.MonitoringRepository;
import com.core.matrix.response.MonitoringResponse;
import com.core.matrix.service.ReportService;
import com.core.matrix.specifications.MonitoringSpecification;
import com.core.matrix.utils.ReportConstants;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
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
@RequestMapping(value = "/api/monitoring")
public class MonitoringResource {

    @Autowired
    private MonitoringRepository monitoringRepository;

    @Autowired
    private ReportService reportService;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity get(
            @RequestParam(required = false, name = "status") String status,
            @RequestParam(required = false, name = "instanciaDoProcesso") String instanciaDoProcesso,
            @RequestParam(required = false, name = "wbcContrato") String wbcContrato,
            @RequestParam(required = false, name = "pontoMedicao") String pontoMedicao,
            @RequestParam(required = false, name = "empresa") String empresa,
            @RequestParam(required = false, name = "atividade") String atividade,
            @RequestParam(required = false, name = "usuario") String usuario,
            @RequestParam(required = true, name = "ano") String ano,
            @RequestParam(required = true, name = "mes") String mes,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {

        try {

            Specification spc = MonitoringSpecification.parameters(status,
                    instanciaDoProcesso,
                    wbcContrato,
                    pontoMedicao,
                    empresa,
                    ano,
                    mes,
                    atividade,
                    usuario);

            Page data = monitoringRepository.findAll(spc, PageRequest.of(page, size, Sort.by("instanciaDoProcesso").ascending()));
            List<MonitoringStatusDTO> statusM = monitoringRepository.status(Long.parseLong(mes), Long.parseLong(ano));
            List<MonitoringFilterDTO> filters = monitoringRepository.filters();
            MonitoringResponse response = new MonitoringResponse(data, statusM, filters);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Logger.getLogger(MonitoringResource.class.getName()).log(Level.SEVERE, "[get]", e);
            return ResponseEntity.status(HttpStatus.resolve(500)).body(e.getMessage());
        }

    }

    @RequestMapping(value = "/export", method = RequestMethod.GET)
    public ResponseEntity export(
            @RequestParam(required = false, name = "status") String status,
            @RequestParam(required = false, name = "instanciaDoProcesso") String instanciaDoProcesso,
            @RequestParam(required = false, name = "wbcContrato") String wbcContrato,
            @RequestParam(required = false, name = "pontoMedicao") String pontoMedicao,
            @RequestParam(required = false, name = "empresa") String empresa,
            @RequestParam(required = false, name = "atividade") String atividade,
            @RequestParam(required = false, name = "usuario") String usuario,
            @RequestParam(required = true, name = "ano") String ano,
            @RequestParam(required = true, name = "mes") String mes,
            @RequestParam(name = "total") int total,
            HttpServletResponse response) {

        try {

             Specification spc = MonitoringSpecification.parameters(status,
                    instanciaDoProcesso,
                    wbcContrato,
                    pontoMedicao,
                    empresa,
                    ano,
                    mes,
                    atividade,
                    usuario);
            Page data = monitoringRepository.findAll(spc, PageRequest.of(0, total, Sort.by("instanciaDoProcesso").ascending()));

            reportService.<Monitoring>export(response, data.getContent(), ReportConstants.ReportType.FULL);

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            Logger.getLogger(MonitoringResource.class.getName()).log(Level.SEVERE, "[get]", e);
            return ResponseEntity.status(HttpStatus.resolve(500)).body(e.getMessage());
        }
    }

}
