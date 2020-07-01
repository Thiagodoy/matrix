/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.resource;

import com.core.matrix.dto.MonitoringContractDTO;
import com.core.matrix.model.MonitoringPoint;
import com.core.matrix.response.MonitoringContractResponse;
import com.core.matrix.response.MonitoringPointResponse;
import com.core.matrix.service.MonitoringPointService;
import com.core.matrix.specifications.MonitoringPointSpecification;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import static com.core.matrix.utils.Url.URL_API_MONITORING_POINT;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
public class MonitoringPointResource extends Resource<MonitoringPoint, MonitoringPointService> {

    public MonitoringPointResource(MonitoringPointService service) {
        super(service);
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity status(
            @RequestParam(name = "month") long month,
            @RequestParam(name = "year") long year,
            @RequestParam(name = "status", required = false) String status
    ) {
        try {

            Specification spc = MonitoringPointSpecification.find(month, year, status);
            Page<MonitoringPoint> responseSummary = this.getService().find(spc, Pageable.unpaged());

            Map<String, Long> sumary = responseSummary.getContent().stream().collect(Collectors.groupingBy(MonitoringPoint::getStatus, Collectors.counting()));

            MonitoringPointResponse resp = new MonitoringPointResponse(responseSummary.getContent(), sumary);

            return ResponseEntity.ok(resp);

        } catch (Exception e) {
            Logger.getLogger(MonitoringPointResource.class.getName()).log(Level.SEVERE, "[status]", e);
            return ResponseEntity.status(HttpStatus.resolve(500)).body(e.getMessage());
        }
    }

    @RequestMapping(value = "/contract", method = RequestMethod.GET)
    public ResponseEntity statusContract(
            @RequestParam(name = "month") long month,
            @RequestParam(name = "year") long year
    ) {
        try {

            List<MonitoringContractDTO> responseSummary = this.getService().getStatusByContract(month, year);

            List<MonitoringContractDTO> contractsParent = responseSummary.parallelStream().filter(c -> c.getHours().equals(-1L)).collect(Collectors.toList());

            contractsParent.parallelStream().forEach(parent -> {

                synchronized (responseSummary) {
                    Map<String, Long> status = responseSummary
                            .stream()
                            .filter(c -> c.getRateio().equals(parent.getRateio()) && !c.getContract().equals(parent.getContract()))
                            .collect(Collectors.groupingBy(MonitoringContractDTO::getStatus, Collectors.counting()));

                   Optional<MonitoringContractDTO> opt = responseSummary
                            .stream()
                            .filter(c -> c.getRateio().equals(parent.getRateio()) && !c.getContract().equals(parent.getContract()))
                            .findFirst();
                    
                    parent.setTaskId(opt.get().getTaskId());
                    parent.setTemplate(opt.get().getTemplate());
                    parent.setTaskName(opt.get().getTaskName());
                    
                    if (status.size() == 1) {
                        status.keySet().forEach(key -> {
                            parent.setStatus(key);
                        });
                    } else {
                        OptionalLong value = status.values().stream().mapToLong(v -> v).max();

                        Optional<Entry<String, Long>> optional = status.entrySet().stream().filter(entry -> {
                            long v1 = value.getAsLong();
                            long v2 = entry.getValue().longValue();
                            return v1 == v2;
                        }).findFirst();

                        parent.setStatus(optional.get().getKey());
                    }
                }

            });

            Map<String, Long> sumary = responseSummary.stream().collect(Collectors.groupingBy(MonitoringContractDTO::getStatus, Collectors.counting()));

            MonitoringContractResponse resp = new MonitoringContractResponse(responseSummary, sumary);

            return ResponseEntity.ok(resp);

        } catch (Exception e) {
            Logger.getLogger(MonitoringPointResource.class.getName()).log(Level.SEVERE, "[statusContract]", e);
            return ResponseEntity.status(HttpStatus.resolve(500)).body(e.getMessage());
        }
    }

}
