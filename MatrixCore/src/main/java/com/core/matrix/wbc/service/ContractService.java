/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.wbc.service;

import com.core.matrix.model.ContractCompInformation;
import com.core.matrix.service.ContractCompInformationService;
import com.core.matrix.service.LogService;
import com.core.matrix.service.MeansurementFileResultService;
import com.core.matrix.service.MeansurementFileService;
import static com.core.matrix.utils.Constants.PROCESS_BILLING_CONTRACT_MESSAGE_EVENT;
import static com.core.matrix.utils.Constants.PROCESS_CONTRACTS_RELOAD_BILLING;
import com.core.matrix.wbc.dto.ContractDTO;
import com.core.matrix.wbc.dto.ContractWbcInformationDTO;
import com.core.matrix.wbc.repository.ContractRepository;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Attachment;
import org.activiti.engine.task.Comment;
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
    private ContractCompInformationService compInformationService;

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
    public List<ContractDTO> listForBilling(List<ContractCompInformation> filter) {

        List<ContractDTO> contracts = this.repository.listForBilling().stream().distinct().collect(Collectors.toList());

        if (Optional.ofNullable(filter).isPresent()) {

            contracts = contracts.stream().filter(intern -> {
                return filter.stream()
                        .anyMatch(xx -> xx.getWbcContract().equals(Long.parseLong(intern.getSNrContrato())));
            }).collect(Collectors.toList());

        }

        return contracts;
    }

    public void reloadProcess(Long contractId) throws Exception {
        List<ContractCompInformation> list = this.compInformationService.listByContract(contractId);

        LocalDate now = LocalDate.now();
        Map<String, Object> variables = new HashMap<>();

        list.stream().forEach(contract -> {

            meansurementFileService
                    .findByWbcContractAndMeansurementPointAndMonthAndYear(
                            contract.getWbcContract(),
                            contract.getMeansurementPoint(),
                            Integer.valueOf(now.getMonthValue()).longValue() - 1,
                            Integer.valueOf(now.getYear()).longValue()
                    ).forEach(file -> {

                        if (!Optional.ofNullable(processInstanceId).isPresent()) {
                            processInstanceId = file.getProcessInstanceId();
                        }

                        List<Attachment> attachments = taskService.getProcessInstanceAttachments(file.getProcessInstanceId());
                        List<Comment> comments = taskService.getProcessInstanceComments(file.getProcessInstanceId());

                        attachments.forEach(att -> {
                            taskService.deleteAttachment(att.getId());
                        });

                        comments.forEach(com -> {
                            taskService.deleteComment(com.getId());
                        });

                        meansurementFileService.delete(file.getId());

                        logService.deleteLogsByProcessInstance(file.getProcessInstanceId());
                        fileResultService.deleteByProcess(file.getProcessInstanceId());

                    });

        });

        try {
            runtimeService.deleteProcessInstance(processInstanceId, "Contract was updated!");
        } catch (Exception e) {
            Logger.getLogger(ContractService.class.getName()).log(Level.WARNING, "[reloadProcess] -> não encontrou o processo para deletar [" + processInstanceId + "]");
        }

        variables.put(PROCESS_CONTRACTS_RELOAD_BILLING, list);
        runtimeService.startProcessInstanceByMessage(PROCESS_BILLING_CONTRACT_MESSAGE_EVENT, variables);

    }

}
