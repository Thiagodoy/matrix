/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.resource;

import com.core.matrix.dto.ContractStatusSummaryDTO;
import com.core.matrix.dto.PointStatusSummaryDTO;
import com.core.matrix.model.MeansurementPointStatus;
import com.core.matrix.response.ContractStatusResponse;
import com.core.matrix.response.PointStatusResponse;
import com.core.matrix.service.ContractMtxStatusService;
import com.core.matrix.service.MeansurementPointStatusService;
import com.core.matrix.specifications.ContractMtxStatusSpecification;
import com.core.matrix.specifications.MeansurementPointStatusSpecification;
import com.core.matrix.utils.ContractStatus;
import static com.core.matrix.utils.Url.URL_API_MONITORING_CONTRACT;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author thiag
 */
@RestController
@RequestMapping(value = URL_API_MONITORING_CONTRACT)
public class MonitoringContractResource {

    @Autowired
    private ContractMtxStatusService contractMtxStatusService;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity status(
            @RequestParam(name = "month") long month,
            @RequestParam(name = "year") long year,
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "contract", required = false) Long contract,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        try {

            Specification spc = ContractMtxStatusSpecification.find(status, contract, month, year);
            PageRequest pageRequest = PageRequest.of(page, size, Sort.by("wbcContract"));

            Page pageResponse = this.contractMtxStatusService.find(spc, pageRequest);

            List<ContractStatusSummaryDTO> summary = this.contractMtxStatusService.summary(month, year);
            ContractStatusResponse response = new ContractStatusResponse(summary, pageResponse);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Logger.getLogger(MonitoringPointResource.class.getName()).log(Level.SEVERE, "[status]", e);
            return ResponseEntity.status(HttpStatus.resolve(500)).body(e.getMessage());
        }
    }

    @RequestMapping(value = "/resetAllStatus", method = RequestMethod.POST)
    public ResponseEntity resetAllStatus() {
        try {

            this.contractMtxStatusService.resetAll();
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            Logger.getLogger(MonitoringPointResource.class.getName()).log(Level.SEVERE, "[resetAllStatus]", e);
            return ResponseEntity.status(HttpStatus.resolve(500)).body(e.getMessage());
        }
    }

}
