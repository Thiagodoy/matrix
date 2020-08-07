/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.wbc.service;


import com.core.matrix.model.ContractMtx;

import com.core.matrix.service.LogService;
import com.core.matrix.service.MeansurementFileResultService;
import com.core.matrix.service.MeansurementFileService;
import com.core.matrix.wbc.dto.ContractDTO;
import com.core.matrix.wbc.dto.ContractWbcInformationDTO;
import com.core.matrix.wbc.repository.ContractRepository;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author thiag
 */
@Service
public class ContractService {

    @Autowired
    private ContractRepository repository;

    @Autowired
    private LogService logService;

    

    @Autowired
    private MeansurementFileService meansurementFileService;

    @Autowired
    private MeansurementFileResultService fileResultService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private RuntimeService runtimeService;

    private String processInstanceId;

    @Transactional(readOnly = true)
    public Page findShortInformation(Long contractId, PageRequest page) {
        return this.repository.shortInfomation(contractId, page);
    }

    @Transactional(readOnly = true)
    public List<ContractDTO> findAll(Long contractId, PageRequest page) {
        return this.repository.fullInformation(contractId);
    }

    @Transactional(readOnly = true)
    public Long countContract() {
        return this.repository.countContract();
    }

    @Transactional(readOnly = true)
    public Optional<ContractWbcInformationDTO> getInformation(Long year, Long month, Long contract) {
        return this.repository.getInformation(year, month, Arrays.asList(contract)).stream().findFirst();
    }
    
    @Transactional(readOnly = true)
    public List<ContractWbcInformationDTO> getInformation(Long year, Long month, List<Long> contract) {        
        if(contract.isEmpty()){
            return Collections.EMPTY_LIST;
        }else{
           return this.repository.getInformation(year, month, contract);     
        }
    }
    
    

    @Transactional(readOnly = true)
    public List<ContractDTO> listForBilling(List<ContractMtx> filter) {

        List<ContractDTO> contracts = this.repository.listForBilling().stream().distinct().collect(Collectors.toList());

        if (Optional.ofNullable(filter).isPresent()) {

            contracts = contracts.stream().filter(intern -> {
                return filter.stream()
                        .anyMatch(xx -> xx.getWbcContract().equals(Long.parseLong(intern.getSNrContrato())));
            }).collect(Collectors.toList());

        }

        return contracts;
    }

   
}
