/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.service;

import com.core.matrix.dto.FileStatusDTO;
import com.core.matrix.dto.MeansurementFileDTO;
import com.core.matrix.dto.MeansurementFileStatusDTO;
import com.core.matrix.model.MeansurementFile;
import com.core.matrix.repository.MeansurementFileRepository;
import com.core.matrix.request.FileStatusLoteRequest;
import com.core.matrix.response.FileStatusBillingResponse;
import com.core.matrix.response.PageResponse;
import com.core.matrix.utils.MeansurementFileStatus;
import com.core.matrix.utils.MeansurementFileType;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author thiag
 */
@Service
public class MeansurementFileService {

    @Autowired
    private MeansurementFileRepository repository;

    @Transactional
    public MeansurementFile saveFile(MeansurementFile file) {
        return this.repository.save(file);
    }

    @Transactional(transactionManager = "matrixTransactionManager")
    public List<MeansurementFile> findByProcessInstanceId(String id) {
        return this.repository.findByProcessInstanceId(id);
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
    public FileStatusBillingResponse statusInit(Long month, Long year, Pageable page) {

        FileStatusBillingResponse response = new FileStatusBillingResponse();

        Page<MeansurementFile> pageResponse = this.repository.findByMonthAndYear(month, year, page);

        List<MeansurementFileDTO> result = pageResponse.getContent().stream().map(f -> new MeansurementFileDTO(f)).collect(Collectors.toList());

        PageResponse<MeansurementFileDTO> responseInfo = new PageResponse<MeansurementFileDTO>(result, (long) pageResponse.getTotalElements(), (long) pageResponse.getSize(), (long) pageResponse.getNumber());

        response.setPage(responseInfo);

        return response;

    }

    @Transactional(readOnly = true)
    public FileStatusBillingResponse statusEnd(FileStatusLoteRequest request) {

        FileStatusBillingResponse response = new FileStatusBillingResponse();

        if (request.isLoadSummary()) {
            List<FileStatusDTO> statusFile = this.repository.getStatusBilling(request.getProcessInstances());
            response.setFileStatusDTOs(statusFile);
        }

        List<MeansurementFileDTO> pageResponse = this.repository.findByProcessInstanceIdIn(request.getProcessInstances())
                .stream()
                .sorted(Comparator.comparing(MeansurementFileDTO::getStatus)
                        .reversed())
                .collect(Collectors.toList());

        PageResponse<MeansurementFileDTO> responseInfo = new PageResponse<MeansurementFileDTO>(pageResponse, (long) pageResponse.size(), (long) pageResponse.size(), 0l);

        response.setPage(responseInfo);

        return response;

    }

}
