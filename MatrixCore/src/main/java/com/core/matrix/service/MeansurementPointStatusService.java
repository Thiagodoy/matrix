/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.service;

import com.core.matrix.model.MeansurementPointStatus;
import com.core.matrix.repository.MeansurementPointStatusRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author thiag
 */
@Component
public class MeansurementPointStatusService implements Observer{

    @Autowired
    private MeansurementPointMtxService pointMtxService;
    
    @Autowired
    private MeansurementPointStatusRepository repository;

    private List<MeansurementPointStatus> pointStatus = new ArrayList<>();

    public void createPointStatus(Long month, Long year) {

        this.pointStatus.clear();       
        
        this.pointMtxService.findAllPoints().forEach(point -> {
            MeansurementPointStatus status = new MeansurementPointStatus(point, month, year);
            status = repository.save(status);            
            status.addObserver(this);
            pointStatus.add(status);
        });

    }

    @Override
    public void update(Observable o, Object arg) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
