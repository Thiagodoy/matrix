/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.service;

import com.core.matrix.model.MeansurementPointStatus;
import com.core.matrix.repository.MeansurementPointStatusRepository;
import com.core.matrix.utils.PointStatus;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Component;

/**
 *
 * @author thiag
 */
@Component
@Scope("singleton") 
public class MeansurementPointStatusService implements Observer {

    @Autowired
    private MeansurementPointMtxService pointMtxService;

    @Autowired
    private MeansurementPointStatusRepository repository;

    private final ThreadPoolExecutor pool = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);

    private Map< String, MeansurementPointStatus> mapPoint = new HashMap<>();

    public void createPointStatus(Long month, Long year) {

        MeansurementPointStatus pointStatus = new MeansurementPointStatus();
        pointStatus.setYear(year);
        pointStatus.setMonth(month);

        ExampleMatcher matcher = ExampleMatcher
                .matching()
                .withIgnoreCase("point", "company", "status", "mount", "hours");

        Example<MeansurementPointStatus> example = Example.of(pointStatus, matcher);

        boolean exists = this.repository.exists(example);

        if (!exists) {
            this.mapPoint.clear();

            this.pointMtxService.findAllPoints().forEach(point -> {
                MeansurementPointStatus status = new MeansurementPointStatus(point, month, year);
                status = repository.save(status);
                status.addObserver(this);
                this.mapPoint.put(point, status);
            });
        }

    }

    @Override
    public void update(Observable o, Object arg) {

        final MeansurementPointStatus meansurementPointStatus = (MeansurementPointStatus) o;

        synchronized (pool) {
            pool.submit(() -> {
                MeansurementPointStatus status = this.repository.save(meansurementPointStatus);
                this.mapPoint.put(status.getPoint(), status);
            });
        }
    }
    
    
    public synchronized MeansurementPointStatus getPoint(String point){
        
        if(this.mapPoint.containsKey(point)){
           return this.mapPoint.get(point);
        }else{            
           MeansurementPointStatus status = this.mapPoint.values().stream().findFirst().get();
           MeansurementPointStatus statusNew = new MeansurementPointStatus(point, status.getMonth(), status.getYear());
           statusNew = this.repository.save(statusNew);
           statusNew.addObserver(this);
           this.mapPoint.put(point, statusNew);
           return statusNew;            
        }
    } 
}
