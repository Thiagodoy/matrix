/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.service;

import com.core.matrix.dto.ContractUnBillingDTO;
import com.core.matrix.dto.FileStatusDTO;
import com.core.matrix.model.ProcessStatusLote;
import com.core.matrix.dto.MeansurementFileStatusDTO;
import com.core.matrix.model.MeansurementFile;
import com.core.matrix.repository.MeansurementFileRepository;
import com.core.matrix.request.FileStatusLoteRequest;
import com.core.matrix.response.FileStatusBillingResponse;
import com.core.matrix.response.PageResponse;
import com.core.matrix.utils.MeansurementFileStatus;
import com.core.matrix.utils.MeansurementFileType;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.core.matrix.repository.ProcessStatusLoteRepository;
import java.util.Arrays;

/**
 *
 * @author thiag
 */
@Service
public class MeansurementFileService {

    @Autowired
    private MeansurementFileRepository repository;

    @Autowired
    private ProcessStatusLoteRepository tORepository;

    @Transactional(transactionManager = "matrixTransactionManager")
    public MeansurementFile saveFile(MeansurementFile file) {
        return this.repository.save(file);
    }
    
    @Transactional(transactionManager = "matrixTransactionManager")
    public void updateAll(List<MeansurementFile> file) {
        this.repository.saveAll(file);
    }

    @Transactional(readOnly = true)
    public List<MeansurementFile> findByProcessInstanceId(String id) {
        return this.repository.findByProcessInstanceId(id);
    }
    
    @Transactional(readOnly = true)
    public List<MeansurementFile> findByProcessInstanceId2(String id) {
        return this.repository.findByProcessInstanceId2(id);
    }

    @Transactional
    public void delete(Long id) {
        this.repository.deleteById(id);
    }

    @Transactional(transactionManager = "matrixTransactionManager")
    public void deleteByProcessInstance(String id) {
        this.repository.deleteByProcessInstanceId(id);
    }

    @Transactional(transactionManager = "matrixTransactionManager")
    public void updateStatus(MeansurementFileStatus status, Long id) {
        this.repository.updateStatus(status, id);
    }
    
    @Transactional(transactionManager = "matrixTransactionManager")
    public void updateStatusByProcessInstanceId(MeansurementFileStatus status, String id) {
        this.repository.updateStatusByProcessInstanceId(status, id);
    }
    
    @Transactional(readOnly = true)
    public List<Long> listIdsByProcessInstanceId(String processInstanceId){
        return this.repository.listIdsByProcessInstanceId(processInstanceId);
    }

    @Transactional(transactionManager = "matrixTransactionManager")
    public void updateFile(String file, Long id) {
        this.repository.updateFile(file, id);
    }

    @Transactional(transactionManager = "matrixTransactionManager")
    public void updateType(MeansurementFileType type, Long id) {
        this.repository.updateType(type, id);
    }

    @Transactional(readOnly = true)
    public MeansurementFile findById(Long id) throws Exception {
        return this.repository.findById(id).orElseThrow(() -> new Exception("Arquivo não encontrado"));
    }

    @Transactional(readOnly = true)
    public List<MeansurementFile> findByWbcContractAndMeansurementPointAndMonthAndYear(Long contract, String point, Long month, Long year) {
        return this.repository.findByWbcContractAndMeansurementPointAndMonthAndYear(contract, point, month, year);
    }

    @Transactional(readOnly = true)
    public List<MeansurementFileStatusDTO> getStatus(Long year, Long month) {
        return this.repository.getStatus(year, month);
    }

    @Transactional(readOnly = true)
    public List<MeansurementFile> findAllFilesWithErrors(String processInstanceId) {
        return this.repository.findAllFilesWithErrors(processInstanceId);
    }

    @Transactional(readOnly = true)
    public boolean hasFilePending(Long year, Long month) {
        return !this.repository.hasFilePending(year, month).isEmpty();
    }

    @Transactional(readOnly = true)
    public boolean exists(Long contract, String meansurementPoint, Long month, Long year) {
        return this.repository.exists(contract, meansurementPoint, month, year).isPresent();
    }
    
    @Transactional(readOnly = true)
    public List<MeansurementFile> exists(List<Long> contract, Long month, Long year) {
        return this.repository.exists(contract, month, year);
    }
    
    @Transactional(readOnly = true)
    public List<Long>contractsWereBilling(List<Long> contract, Long month, Long year) {
        return this.repository.contractUnbilling(contract, month, year).parallelStream().mapToLong(ContractUnBillingDTO::getContractWbc).distinct().boxed().collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public boolean contractHasBilling(Long contract, Long month, Long year){
        return this.repository.contractHasBilling(contract, month, year);
    }
    
    @Transactional(readOnly = true)
    public FileStatusBillingResponse statusInit(Long month, Long year, Pageable page) {

        FileStatusBillingResponse response = new FileStatusBillingResponse();

        Page<MeansurementFile> pageResponse = this.repository.findByMonthAndYearAndStatusNotIn(month, year,Arrays.asList(MeansurementFileStatus.APPROVED,MeansurementFileStatus.SUCCESS), page);

        List<ProcessStatusLote> result = pageResponse.getContent().stream().map(f -> new ProcessStatusLote(f)).collect(Collectors.toList());

        PageResponse<ProcessStatusLote> responseInfo = new PageResponse<ProcessStatusLote>(result, (long) pageResponse.getTotalElements(), (long) pageResponse.getSize(), (long) pageResponse.getNumber());

        response.setPage(responseInfo);

        return response;

    }

    @Transactional
    public void generateStatus(FileStatusLoteRequest request, String processIntanceId) {

        List<ProcessStatusLote> pageResponse = this.repository.findByProcessInstanceIdIn(request.getProcessInstances())
                .stream()
                .sorted(Comparator.comparing(ProcessStatusLote::getStatus)
                        .reversed())
                .collect(Collectors.toList());
        
        pageResponse.forEach(p->{
            p.setProcessInstanceIdLote(processIntanceId);
        });

        tORepository.saveAll(pageResponse);

    }

    @Transactional(readOnly = true)
    public FileStatusBillingResponse statusEnd(String processInstance, boolean loadSumary, Pageable page) {

        FileStatusBillingResponse response = new FileStatusBillingResponse();

        if (loadSumary) {

            List<FileStatusDTO> statusFile = new ArrayList<>();
            List<ProcessStatusLote> pageResponse1 = tORepository.findByProcessInstanceIdLote(processInstance);
            Map<String, Long> mapSummary = pageResponse1.stream().collect(Collectors.groupingBy(ProcessStatusLote::getStatus, Collectors.counting()));
            mapSummary.keySet().stream().forEach(key -> {
                FileStatusDTO fileStatusDTO = new FileStatusDTO(mapSummary.get(key), key);
                statusFile.add(fileStatusDTO);
            });

            response.setFileStatusDTOs(statusFile);

        }

        Page<ProcessStatusLote> pageResponse = tORepository.findByProcessInstanceIdLote(processInstance, page);
        
        PageResponse<ProcessStatusLote> responseInfo = new PageResponse<ProcessStatusLote>(pageResponse.getContent(), (long) pageResponse.getTotalElements(), (long) pageResponse.getNumberOfElements(), (long) pageResponse.getNumber());
        response.setPage(responseInfo);

        return response;

    }
    
    
    @Transactional(readOnly = true)
    public List<MeansurementFile> listByContractsAndMonthAndYear(List<Long> contracts, Long month, Long year){
        return this.repository.findByMonthAndYearAndWbcContractIn(month, year, contracts);
    }

}
