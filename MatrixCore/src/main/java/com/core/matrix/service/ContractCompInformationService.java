/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.service;

import com.core.matrix.dto.ContractInformationDTO;
import com.core.matrix.model.ContractCompInformation;
import com.core.matrix.model.MeansurementFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.core.matrix.repository.ContractCompInformationRepository;
import static com.core.matrix.utils.Constants.PROCESS_BILLING_CONTRACT_MESSAGE_EVENT;
import static com.core.matrix.utils.Constants.PROCESS_CONTRACTS_RELOAD_BILLING;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Attachment;
import org.activiti.engine.task.Comment;
import org.springframework.transaction.annotation.Propagation;

/**
 *
 * @author thiag
 */
@Service
public class ContractCompInformationService {

    @Autowired
    private ContractCompInformationRepository repository;

    @Autowired
    private MeansurementFileService meansurementFileService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private LogService logService;

    @Autowired
    private MeansurementFileResultService fileResultService;

    @Autowired
    private RuntimeService runtimeService;

    @Transactional(transactionManager = "matrixTransactionManager")
    public void save(ContractCompInformation information) throws Exception {

        Optional<ContractCompInformation> entity = this.repository
                .findById(information.getWbcContract());

        if (entity.isPresent()) {
            this.update(information);
        } else {
            if (information.getProinfas() != null) {
                information.getProinfas().stream().forEach(p -> {
                    p.setWbcContract(information.getWbcContract());
                    p.setMeansurementPoint(information.getMeansurementPoint());
                });
            }

            this.repository.save(information);
        }

    }

    @Transactional(transactionManager = "matrixTransactionManager")
    public void update(ContractCompInformation information) throws Exception {

        ContractCompInformation entity = this.repository
                .findById(information.getWbcContract())
                .orElseThrow(() -> new Exception("Não foi encontrado nenhuma informação adicional para o contrato"));

        entity.update(information);
        this.repository.save(entity);

    }

    @Transactional(transactionManager = "matrixTransactionManager", propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public List<ContractCompInformation> listByContract(Long contractId) throws Exception {

        Optional<ContractCompInformation> opt = this.repository.findByWbcContract(contractId);

        if (opt.isPresent() && opt.get().getIsApportionment() == 0L) {
            return Arrays.asList(opt.get());
        } else if (opt.isPresent() && opt.get().getIsApportionment() == 1L) {

            ContractCompInformation contractCompInformation = opt.get();
            Optional<Long> isSon = Optional.ofNullable(contractCompInformation.getCodeContractApportionment());

            if (isSon.isPresent() && isSon.get() > 0L) {
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

    @Transactional(readOnly = true)
    public boolean isConsumerUnit(Long contractWbc) {

        Optional<ContractCompInformation> opt = this.repository.findByWbcContract(contractWbc);

        return (opt.isPresent() && opt.get().getIsConsumerUnit() != null && opt.get().getIsConsumerUnit().equals("1"));
    }

    public Optional<ContractCompInformation> findByWbcContractAndMeansurementPoint(Long contract, String point) {
        return this.repository.findByWbcContractAndMeansurementPoint(contract, point);
    }

    public Optional<ContractCompInformation> findByMeansurementPoint(String point) {
        return this.repository.findByMeansurementPoint(point);
    }

    @Transactional(transactionManager = "matrixTransactionManager")
    public void reloadProcess(Long contractId) throws Exception {

        List<ContractCompInformation> list = this.listByContract(contractId);

        if (list.isEmpty()) {
            throw new Exception("Contrato sem informação complementar!");
        }

        LocalDate now = LocalDate.now();
        Long month = (long) now.minusMonths(1).getMonthValue();
        Long year = (long) now.getYear();
        List<Long> contracts = list.stream().mapToLong(ContractCompInformation::getWbcContract).boxed().collect(Collectors.toList());

        List<MeansurementFile> files = meansurementFileService.listByContractsAndMonthAndYear(contracts, month, year);
        String processInstanceID = null;

        if (!files.isEmpty()) {

            processInstanceID = files.stream().findFirst().get().getProcessInstanceId();
            
            fileResultService.deleteByProcess(processInstanceID);
            meansurementFileService.deleteByProcessInstance(processInstanceID);
            logService.deleteLogsByProcessInstance(processInstanceID);            

            List<Attachment> attachments = taskService.getProcessInstanceAttachments(processInstanceID);
            List<Comment> comments = taskService.getProcessInstanceComments(processInstanceID);

            attachments.forEach(att -> {
                taskService.deleteAttachment(att.getId());
            });

            comments.forEach(com -> {
                taskService.deleteComment(com.getId());
            });
            
            try {
                runtimeService.deleteProcessInstance(processInstanceID, "Contract was updated!");
            } catch (Exception e) {
            }
            
        }        
        

        Map<String, Object> variables = new HashMap<>();
        variables.put(PROCESS_CONTRACTS_RELOAD_BILLING, list);
        runtimeService.startProcessInstanceByMessage(PROCESS_BILLING_CONTRACT_MESSAGE_EVENT, variables);

    }

}
