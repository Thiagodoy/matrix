/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.service;

import com.core.matrix.dto.ContractPointDTO;
import com.core.matrix.model.ContractMtx;
import com.core.matrix.model.MeansurementPointMtx;
import com.core.matrix.repository.ContractMtxRepository;
import com.core.matrix.response.ContractMtxResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author thiag
 */
@org.springframework.stereotype.Service
public class ContractMtxService extends Service<ContractMtx, ContractMtxRepository> {

    @Autowired
    private MeansurementPointMtxService pointMtxService;

    public ContractMtxService(ContractMtxRepository repositoy) {
        super(repositoy);
    }

    @Transactional
    public void associateContractToPoint(Long contract, String point) throws Exception {

        MeansurementPointMtx pointMtx = this.pointMtxService.getByPoint(point);
        ContractMtx contractMtx = this.findByWbcContract(contract);
        pointMtx.getContracts().add(contractMtx);

        pointMtxService.update(pointMtx);

    }

    @Transactional(readOnly = true)
    public ContractMtx findByWbcContract(Long contract) throws Exception {
        return this.repository.findByWbcContract(contract).orElseThrow(() -> new Exception("Not found entity!"));
    }

    public ContractMtxResponse findAll(Long contract) throws Exception {

        ContractMtx contractMtx = this.findByWbcContract(contract);
        List<ContractMtx> contracts = new ArrayList<>();

        if (!contractMtx.isApportionment()) {
            contracts = Arrays.asList(contractMtx);
        } else if (contractMtx.isApportionment()) {

            Optional<Long> isSon = Optional.ofNullable(contractMtx.getCodeContractApportionment());

            if (isSon.isPresent() && isSon.get() > 0L) {
                //load all sons
                contracts = this.repository.findByCodeContractApportionment(isSon.get());
                ContractMtx rateioFather = this.repository.findByCodeWbcContract(isSon.get())
                        .orElseThrow(() -> new Exception("Not found contract Father"));

                contracts.add(rateioFather);

            } else {
                contracts = this.repository.findByCodeContractApportionment(contractMtx.getCodeWbcContract());
                contracts.add(contractMtx);
            }

        }

        List<Long> ids = contracts.stream().mapToLong(ContractMtx::getCodeWbcContract).boxed().collect(Collectors.toList());
        List<ContractPointDTO> associations = this.repository.associations(ids);

        ContractMtxResponse response = new ContractMtxResponse();
        response.setAssociations(associations);
        response.setContracts(contracts);
        return response;

    }

}
