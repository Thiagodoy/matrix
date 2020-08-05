/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.resource;

import com.core.matrix.dto.ContractUnBillingDTO;
import com.core.matrix.dto.MonitoringFilterDTO;
import com.core.matrix.dto.MonitoringStatusDTO;
import com.core.matrix.dto.UnbilledContractDTO;
import com.core.matrix.model.Monitoring;
import com.core.matrix.repository.MonitoringRepository;
import com.core.matrix.response.MonitoringResponse;
import com.core.matrix.service.MeansurementFileService;
import com.core.matrix.service.ReportService;
import com.core.matrix.specifications.MonitoringSpecification;
import com.core.matrix.utils.ReportConstants;
import com.core.matrix.wbc.service.ContractService;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;
import org.joda.time.LocalDate;
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

    @Autowired
    private ContractService contractService;

    @Autowired
    private MeansurementFileService fileService;

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

            Page<Monitoring> data = monitoringRepository.findAll(spc, PageRequest.of(page, size));
            List<MonitoringStatusDTO> statusM = monitoringRepository.status(Long.parseLong(mes), Long.parseLong(ano));
            List<MonitoringFilterDTO> filters = monitoringRepository.filters();

            List<Long> contracts = data.getContent()
                    .stream()
                    .map(Monitoring::getWbcContrato)
                    .filter(Objects::nonNull)
                    .mapToLong(Long::valueOf)
                    .boxed()
                    .collect(Collectors.toList());

            contractService.getInformation(Long.parseLong(ano), Long.parseLong(mes), contracts).stream().forEach(i -> {

                Optional<Monitoring> opt = data.getContent().stream().filter(cc -> cc.getWbcContrato().equals(i.getNrContract())).findFirst();
                if (opt.isPresent()) {
                    opt.get().setValorEsperadoWbc(i.getQtdBillingWbc());
                }

            });

            MonitoringResponse response = new MonitoringResponse(data, statusM, filters);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Logger.getLogger(MonitoringResource.class.getName()).log(Level.SEVERE, "[get]", e);
            return ResponseEntity.status(HttpStatus.resolve(500)).body(e.getMessage());
        }

    }

    @RequestMapping(value = "/unbilled/contract", method = RequestMethod.GET)
    public ResponseEntity unbilledContract() {
        try {

            LocalDate monthBilling = LocalDate.now().minusMonths(1);

            final Long month = Integer.valueOf(monthBilling.getMonthOfYear()).longValue();
            final Long year = Integer.valueOf(monthBilling.getYear()).longValue();

            List<UnbilledContractDTO> contracts = this.contractService.listForBilling(null)
                    .parallelStream()
                    .map(c -> new UnbilledContractDTO(c))
                    .collect(Collectors.toList());

            List<Long> idContracts = contracts
                    .stream()
                    .mapToLong(UnbilledContractDTO::getContractWbc)
                    .distinct()
                    .boxed()
                    .collect(Collectors.toList());

            List<Long> contractsWereBilling = fileService.contractsWereBilling(idContracts, month, year);

            contracts.removeIf(contract -> contractsWereBilling.stream().anyMatch(bi -> bi.equals(contract.getContractWbc())));

            List<UnbilledContractDTO> contractsRateio = contracts.stream().filter(contract -> contract.isRateio()).collect(Collectors.toList());

            contracts.removeIf(contract -> contractsRateio.stream().anyMatch(bi -> bi.getContractWbc().equals(contract.getContractWbc())));

            contractsRateio
                    .stream()
                    .parallel()
                    .filter(contract -> contract.getContractRateio() == null)
                    .forEach(contract -> {
                        if (!fileService.contractHasBilling(contract.getContractWbc(), month, year)) {
                            contracts.add(contract);
                        }
                    });

            return ResponseEntity.ok(new HashSet(contracts));
        } catch (Exception e) {
            Logger.getLogger(MonitoringResource.class.getName()).log(Level.SEVERE, "[unbilledContract]", e);
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
