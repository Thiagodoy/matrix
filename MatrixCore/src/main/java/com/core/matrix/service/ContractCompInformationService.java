/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.service;

import com.core.matrix.dto.ContractInformationDTO;
import com.core.matrix.model.ContractCompInformation;
import com.core.matrix.model.ContractProInfa;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.core.matrix.repository.ContractCompInformationRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author thiag
 */
@Service
public class ContractCompInformationService {

    @Autowired
    private ContractCompInformationRepository repository;

    @Autowired
    private ContractProInfaService proInfaService;

    @Transactional
    public void save(ContractCompInformation information) {

        if (information.getProinfas() != null) {
            information.getProinfas().stream().forEach(p -> {
                p.setWbcContract(information.getWbcContract());
                p.setMeansurementPoint(information.getMeansurementPoint());
            });
        }

        this.repository.save(information);
    }

    @Transactional
    public void update(ContractCompInformation information) throws Exception {

        ContractCompInformation.IdClass idClass = new ContractCompInformation.IdClass();
        idClass.setMeansurementPoint(information.getMeansurementPoint());
        idClass.setWbcContract(information.getWbcContract());

        ContractCompInformation entity = this.repository
                .findById(idClass)
                .orElseThrow(() -> new Exception("Não foi encontrado nenhuma informação adicional para o contrato"));

        entity.update(information);

        this.repository.save(entity);

        ContractProInfa.IdClass id = new ContractProInfa.IdClass();
        id.setMeansurementPoint(information.getMeansurementPoint());
        id.setWbcContract(information.getWbcContract());
        proInfaService.delete(id);

        information.getProinfas().forEach(c -> {

            c.setMeansurementPoint(id.getMeansurementPoint());
            c.setWbcContract(c.getWbcContract());

        });
        
        proInfaService.saveAll(information.getProinfas());

    }

    @Transactional
    public List<ContractCompInformation> listByContract(Long contractId) {

        Optional<ContractCompInformation> opt = this.repository.findByWbcContract(contractId);

        if (opt.isPresent()) {
            ContractCompInformation contractCompInformation = opt.get();

            Optional<Long> isSon = Optional.ofNullable(contractCompInformation.getCodeContractApportionment());

            if (isSon.isPresent()) {
                return this.repository.findByCodeContractApportionment(contractId);
            } else {
                return Arrays.asList(opt.get());
            }

        } else {
            return new ArrayList<>();
        }

    }

    @Transactional(readOnly = true)
    public Optional<ContractInformationDTO> listByPoint(String point) {
        return this.repository.listByPoint(point);
    }

    @Transactional(readOnly = true)
    public Optional<ContractCompInformation> findByContract(Long contract) {
        return this.repository.findByWbcContract(contract);
    }

    public Optional<ContractCompInformation> findByWbcContractAndMeansurementPoint(Long contract, String point) {
        return this.findByWbcContractAndMeansurementPoint(contract, point);
    }

}
