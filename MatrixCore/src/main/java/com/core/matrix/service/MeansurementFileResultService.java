/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.service;

import com.core.matrix.dto.MeansurementFileResultStatusDTO;
import com.core.matrix.model.MeansurementFileResult;
import com.core.matrix.repository.MeansurementFileResultRepository;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author thiag
 */
@Service
public class MeansurementFileResultService {

    @Autowired
    private MeansurementFileResultRepository repository;

    @Transactional
    public void save(MeansurementFileResult result) {
        this.repository.save(result);
    }

    @Transactional
    public void update(MeansurementFileResult result) throws Exception {

        MeansurementFileResult fileResult = this.repository
                .findById(result.getId())
                .orElseThrow(() -> new Exception("Não foi encontrado nenhum resultado!"));

        fileResult.update(result);

        this.repository.save(result);
    }

    @Transactional(readOnly = true)
    public List<MeansurementFileResult> getResult(String id) {
        return this.repository.findByIdProcess(id)
                .stream()
                .sorted(Comparator.comparing(MeansurementFileResult::getContractParent)
                        .reversed())
                .collect(Collectors.toList());
    }

    @Transactional(transactionManager = "matrixTransactionManager")
    public void deleteByProcess(String id) {
        this.repository.deleteByIdProcess(id);
    }

    @Transactional(readOnly = true)
    public List<MeansurementFileResultStatusDTO> getStatusBilling(Long year, Long month) {
        return this.repository.getStatusBilling(year, month)
                .stream()
                .sorted(Comparator.comparing(MeansurementFileResultStatusDTO::getId))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MeansurementFileResult> findByIds(List<Long> ids) {
        return this.repository.findAllById(ids);
    }

    @Transactional(transactionManager = "matrixTransactionManager")
    public void updateToExported(Long id) {
        this.repository.updateToExported(id);
    }

}
