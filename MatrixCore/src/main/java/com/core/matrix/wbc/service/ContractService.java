/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.wbc.service;

import com.core.matrix.model.ContractCompInformation;
import static com.core.matrix.utils.Constants.PROCESS_CONTRACTS_RELOAD_BILLING;
import com.core.matrix.wbc.dto.ContractDTO;
import com.core.matrix.wbc.dto.ContractWbcInformationDTO;
import com.core.matrix.wbc.repository.ContractRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
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
        return this.repository.getInformation(year, month, contract);
    }

    @Transactional(readOnly = true)
    public List<ContractDTO> listForBilling(List<ContractCompInformation> filter) {

        List<ContractDTO> contracts = this.repository.listForBilling();

        if (Optional.ofNullable(filter).isPresent()) {

            contracts = contracts.stream().filter(intern -> {
                return filter.stream()
                        .filter(xx -> xx.getWbcContract().equals(Long.parseLong(intern.getSNrContrato())))
                        .findFirst()
                        .isPresent();

            }).collect(Collectors.toList());

        }

        return contracts;
    }
}
