/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.wbc.service;

import com.core.matrix.wbc.model.MeansurementPoint;
import com.core.matrix.wbc.repository.MeansurementPointRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author thiag
 */
@Service
public class MeansurementPointService {

    @Autowired
    private MeansurementPointRepository repository;

    @Transactional(readOnly = true)
    public boolean existsPoint(String point) {
        return this.repository.findByCode(point).isPresent();
    }
    
    @Transactional(readOnly = true)
    public MeansurementPoint getPoint(String point) {
        return this.repository.findByCode(point).get();
    }

}
