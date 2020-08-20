/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.service;

import com.core.matrix.dto.MeansurementFileResultStatusDTO;
import com.core.matrix.model.MeansurementFileResult;
import com.core.matrix.repository.MeansurementFileResultRepository;
import com.core.matrix.wbc.service.ContractService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author thiag
 */
@Service
public class MeansurementFileResultService {

    @Autowired
    private MeansurementFileResultRepository repository;

    @Autowired
    private ContractService contractService;

    @Transactional
    public void save(MeansurementFileResult result) {
        this.repository.save(result);
    }

    @Transactional
    public void saveAll(List<MeansurementFileResult> result) {
        this.repository.saveAll(result);
    }

    @Transactional
    public void update(MeansurementFileResult result) throws Exception {

        MeansurementFileResult fileResult = this.repository
                .findById(result.getId())
                .orElseThrow(() -> new Exception("Não foi encontrado nenhum resultado!"));

        fileResult.update(result);

        this.repository.save(result);
    }

    @Transactional(readOnly = true)
    public List<MeansurementFileResult> getResult(String id) {
        return this.repository.findByIdProcess(id)
                .stream()
                .sorted(Comparator.comparing(MeansurementFileResult::getContractParent)
                        .reversed())
                .collect(Collectors.toList());
    }

    @Transactional(transactionManager = "matrixTransactionManager")
    public void deleteByProcess(String id) {
        this.repository.deleteByIdProcess(id);
    }

    @Transactional(readOnly = true)
    public List<MeansurementFileResultStatusDTO> getStatusBilling(Long year, Long month) {
        
        
        LocalDate monthBilling = LocalDate.of(year.intValue(), month.intValue(), 1);
        
        LocalDateTime start = monthBilling.atStartOfDay();
        LocalDateTime end = LocalDateTime.of(year.intValue(), month.intValue(), monthBilling.getMonth().maxLength(), 23, 59);
        

        List<MeansurementFileResultStatusDTO> results = this.repository.getStatusBilling(start, end)
                .stream()                
                .collect(Collectors.toList());
        
         List<Long> contracts = results
                    .stream()
                    .map(MeansurementFileResultStatusDTO::getWbcContract)
                    .filter(Objects::nonNull)
                    .mapToLong(Long::valueOf)
                    .boxed()
                    .collect(Collectors.toList());
         
         contractService.getInformation(year, month, contracts).stream().forEach(i -> {
            Optional<MeansurementFileResultStatusDTO> opt = results.stream().filter(cc -> cc.getWbcContract().equals(Long.valueOf(i.getNrContract()))).findFirst();
            if (opt.isPresent()) {
                opt.get().setBillingWbc(i.getQtdBillingWbc());
            }
        });         
        
        return results;
    }

    @Transactional(readOnly = true)
    public List<MeansurementFileResult> findByIds(List<Long> ids) {
        return this.repository.findAllById(ids);
    }

    @Transactional(transactionManager = "matrixTransactionManager")
    public void updateToExported(Long id) {
        this.repository.updateToExported(id);
    }
    
    @Transactional(transactionManager = "matrixTransactionManager")
    public void updateToExportedByProcessInstance(String id) {
        this.repository.updateToExportedByProcessInstance(id);
    }

    public List<MeansurementFileResult> findByIdProcess(String id) {
        return this.repository.findByIdProcess(id);
    }

}
