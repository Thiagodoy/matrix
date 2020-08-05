/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.service;

import com.core.matrix.dto.ContractPointDTO;
import com.core.matrix.exceptions.EntityNotFoundException;
import com.core.matrix.model.ContractCompInformation;
import com.core.matrix.model.ContractMtx;
import com.core.matrix.model.MeansurementFile;
import com.core.matrix.model.MeansurementPointMtx;
import com.core.matrix.repository.ContractMtxRepository;
import com.core.matrix.response.ContractMtxResponse;
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
    
    @Autowired
    private MeansurementFileService meansurementFileService;
    
    @Autowired
    private MeansurementFileResultService fileResultService;
    
    @Autowired
    private LogService logService;
    
    @Autowired
    private TaskService taskService;
    
    @Autowired
    private RuntimeService runtimeService;
    

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
    
    @Transactional
    public void unAssociateContractToPoint(Long contract, String point) throws Exception {

        MeansurementPointMtx pointMtx = this.pointMtxService.getByPoint(point);
        ContractMtx contractMtx = this.findByWbcContract(contract);
        pointMtx.getContracts().remove(contractMtx);

        pointMtxService.update(pointMtx);

    }

    @Transactional(readOnly = true)
    public ContractMtx findByWbcContract(Long contract) throws EntityNotFoundException {

        Optional op = this.repository.findByWbcContract(contract);

        ContractMtx contractMtx = this.repository.findByWbcContract(contract).orElseThrow(() -> new EntityNotFoundException());

        Optional<ContractPointDTO> opt = this.repository.associations(Arrays.asList(contractMtx.getWbcContract())).stream().findFirst();

        if (opt.isPresent()) {
            contractMtx.setPointAssociated(opt.get().getPoint());
        }

        return contractMtx;
    }

    public ContractMtxResponse findAll(Long contract) throws Exception {

        ContractMtx contractMtx = this.findByWbcContract(contract);
        final List<ContractMtx> contracts = new ArrayList<>();

        if (!contractMtx.isApportionment()) {
            contracts.add(contractMtx);
        } else if (contractMtx.isApportionment()) {

            Optional<Long> isSon = Optional.ofNullable(contractMtx.getCodeContractApportionment());

            if (isSon.isPresent() && isSon.get() > 0L) {
                //load all sons
                contracts.addAll(this.repository.findByCodeContractApportionment(isSon.get()));
                ContractMtx rateioFather = this.repository.findByCodeWbcContract(isSon.get())
                        .orElseThrow(() -> new Exception("Not found contract Father"));

                contracts.add(rateioFather);

            } else {
                contracts.addAll(this.repository.findByCodeContractApportionment(contractMtx.getCodeWbcContract()));
                contracts.add(contractMtx);
            }

        }

        List<Long> ids = contracts.stream().mapToLong(ContractMtx::getWbcContract).boxed().collect(Collectors.toList());
        List<ContractPointDTO> associations = this.repository.associations(ids);

        associations.forEach(ass -> {
            Optional<ContractMtx> optContract = contracts.stream().filter(c -> c.getWbcContract().equals(ass.getContract())).findFirst();
            if (optContract.isPresent()) {
                optContract.get().setPointAssociated(ass.getPoint());
            }
        });

        ContractMtxResponse response = new ContractMtxResponse();
        response.setAssociations(associations);
        response.setContracts(contracts);
        return response;

    }

    public boolean isConsumerUnit(Long contractId){        
        
        Optional<ContractMtx> opt =  this.repository.findByWbcContract(contractId);
        
        if(opt.isPresent()){
            return opt.get().isConsumerUnit();
        }else{
            return false;
        }
    }
    
    
     @Transactional(transactionManager = "matrixTransactionManager")
    public void reloadProcess(Long contractId) throws Exception {

        List<ContractMtx> list = this.findAll(contractId).getContracts();

        if (list.isEmpty()) {
            throw new EntityNotFoundException("Contrato não cadastrado na base da [ Matrix ]");
        }
        
        if(list.size() == 1){
            ContractMtx contractMtx = list.get(0);            
            contractMtx.getPoint();
        }

        LocalDate now = LocalDate.now();
        Long month = (long) now.minusMonths(1).getMonthValue();
        Long year = (long) now.getYear();
        List<Long> contracts = list.stream().mapToLong(ContractMtx::getWbcContract).boxed().collect(Collectors.toList());

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
