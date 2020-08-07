/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.service;

import com.core.matrix.dto.ContractStatusSummaryDTO;
import com.core.matrix.exceptions.EntityNotFoundException;
import com.core.matrix.model.ContractMtx;
import com.core.matrix.model.ContractMtxStatus;
import com.core.matrix.repository.ContractMtxStatusRepository;
import com.core.matrix.utils.ContractStatus;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author thiag
 */
@Component
@Scope("singleton")
public class ContractMtxStatusService implements Observer {

    @Autowired
    private ContractMtxService contractMtxService;

    @Autowired
    private ContractMtxStatusRepository contractMtxStatusRepository;

    private final ThreadPoolExecutor pool = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);

    private Map< Long, ContractMtxStatus> mapContracts = new HashMap<>();

    public void createContractStatus(Long month, Long year) {

        ContractMtxStatus contractMtxStatus = new ContractMtxStatus();
        contractMtxStatus.setYear(year);
        contractMtxStatus.setMonth(month);

        Example<ContractMtxStatus> example = Example.of(contractMtxStatus);

        boolean exists = this.contractMtxStatusRepository.exists(example);

        if (!exists) {
            this.mapContracts.clear();

            this.contractMtxService.findAll()
                    .stream()
                    .filter(c -> !c.isFather())
                    .forEach(contract -> {
                        ContractMtxStatus status = new ContractMtxStatus(contract);
                        status = contractMtxStatusRepository.save(status);
                        status.addObserver(this);
                        this.mapContracts.put(contract.getWbcContract(), status);
                    });
        } else {
            if (this.mapContracts.isEmpty()) {
                this.contractMtxStatusRepository.findByMonthAndYear(month, year).forEach(status -> {
                    status.addObserver(this);
                    this.mapContracts.put(status.getWbcContract(), status);
                });
            }
        }

    }

    @Override
    public void update(Observable o, Object arg) {

        final ContractMtxStatus status = (ContractMtxStatus) o;

        synchronized (pool) {
            pool.submit(() -> {
                this.updateContract(status);
            });
        }
    }

    @Transactional
    private void updateContract(ContractMtxStatus status) {
        status = this.contractMtxStatusRepository.save(status);
        status.addObserver(this);
        this.mapContracts.put(status.getWbcContract(), status);
    }

    public void resetPoint(Long contract) {

        if (this.mapContracts.containsKey(contract)) {
            ContractMtxStatus contractStatus = this.mapContracts.get(contract);
            contractStatus.setAmountGross(0D);
            contractStatus.setStatus(ContractStatus.NO_BILL);
            contractStatus.setReasonStatus("");
            contractStatus.setAmountGross(0D);
            contractStatus.setAmountLiquid(0D);
            contractStatus.forceUpdate();
        }
    }

    @Transactional
    public synchronized Optional<ContractMtxStatus> getContract(Long contract) {

        if (this.mapContracts.containsKey(contract)) {
            return Optional.ofNullable(this.mapContracts.get(contract));
        } else if (Optional.ofNullable(contract).isPresent()) {
            ContractMtx contractMtx;
            try {
                contractMtx = this.contractMtxService.findByWbcContract(contract);
                ContractMtxStatus statusNew = new ContractMtxStatus(contractMtx);
                statusNew = this.contractMtxStatusRepository.save(statusNew);
                statusNew.addObserver(this);
                this.mapContracts.put(statusNew.getWbcContract(), statusNew);
                return Optional.ofNullable(statusNew);
            } catch (EntityNotFoundException ex) {
                return Optional.empty();
            }

        } else {
            return Optional.empty();
        }
    }

    @Transactional
    public Page<ContractMtxStatus> find(Specification s, Pageable page) {
      return this.contractMtxStatusRepository.findAll(s, page);
    }

    @Transactional
    public List<ContractStatusSummaryDTO> summary(Long month, Long year) {
        return this.contractMtxStatusRepository.summary(month, year);
    }
    
    public void shutdown(){
        pool.shutdown();
    }
}
