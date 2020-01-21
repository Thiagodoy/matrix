/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.service;

import com.core.matrix.model.ContractMeasurementPoint;
import com.core.matrix.repository.ContractMeasurementPointRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author thiag
 */
@Service
public class ContractMeasurementPointService {

    @Autowired
    private ContractMeasurementPointRepository repository;

    @Transactional
    public void save(ContractMeasurementPoint entity) {
        this.repository.save(entity);
    }

}
