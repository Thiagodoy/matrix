/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.service;

import com.core.matrix.dto.ContractInformationDTO;
import com.core.matrix.model.ContractCompInformation;
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

        ContractCompInformation entity = this.repository
                .findById(information.getWbcContract())
                .orElseThrow(() -> new Exception("Não foi encontrado nenhuma informação adicional para o contrato"));

        this.repository.delete(entity);        
        this.save(information);
        
    }

    @Transactional
    public List<ContractCompInformation> listByContract(Long contractId) throws Exception {

        Optional<ContractCompInformation> opt = this.repository.findByWbcContract(contractId);

        if (opt.isPresent() && opt.get().getIsApportionment() == 0L) {
            return Arrays.asList(opt.get());
        } else if (opt.isPresent() && opt.get().getIsApportionment() == 1L) {

            ContractCompInformation contractCompInformation = opt.get();
            Optional<Long> isSon = Optional.ofNullable(contractCompInformation.getCodeContractApportionment());

            if (isSon.isPresent()) {
                //load all sons
                List<ContractCompInformation> rateioSon = this.repository.findByCodeContractApportionment(isSon.get());
                ContractCompInformation rateioFather = this.repository.findByCodeWbcContract(isSon.get())
                        .orElseThrow(() -> new Exception("Não foi possivel localizar o contrato pai!"));

                rateioSon.add(rateioFather);
                return rateioSon;
            } else {

                List<ContractCompInformation> rateioSon = this.repository.findByCodeContractApportionment(contractCompInformation.getCodeWbcContract());
               

                rateioSon.add(contractCompInformation);
                return rateioSon;

            }

        }else{
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
        return this.repository.findByWbcContractAndMeansurementPoint(contract, point);
    }
    
    public Optional<ContractCompInformation> findByMeansurementPoint(String point) {
        return this.repository.findByMeansurementPoint(point);
    }

}
