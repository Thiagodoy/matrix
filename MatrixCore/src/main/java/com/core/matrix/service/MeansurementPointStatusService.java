/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.service;

import com.core.matrix.dto.PointStatusSummaryDTO;
import com.core.matrix.model.MeansurementPointStatus;
import com.core.matrix.repository.MeansurementPointStatusRepository;
import com.core.matrix.utils.PointStatus;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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
        } else {
            if (this.mapPoint.isEmpty()) {
                this.repository.findByMonthAndYear(month, year).forEach(point -> {
                    point.addObserver(this);
                    this.mapPoint.put(point.getPoint(), point);
                });
            }
        }

    }

    @Override
    public void update(Observable o, Object arg) {

        final MeansurementPointStatus meansurementPointStatus = (MeansurementPointStatus) o;

        synchronized (pool) {
            pool.submit(() -> {
                this.updatePOint(meansurementPointStatus);
            });
        }
    }

    @Transactional
    private void updatePOint(MeansurementPointStatus status) {
        MeansurementPointStatus up = this.repository.save(status);
        up.addObserver(this);
        this.mapPoint.put(status.getPoint(), up);
    }

    public void resetPoint(String point) {

        if (this.mapPoint.containsKey(point)) {
            MeansurementPointStatus pointStatus = this.mapPoint.get(point);
            pointStatus.setHours(0L);
            pointStatus.setStatus(PointStatus.NO_READ);
            pointStatus.setMountScde(0D);
            pointStatus.setCompany("");
            pointStatus.setAmountGross(0D);
            pointStatus.setAmountLiquid(0D);
            pointStatus.forceUpdate();
        }
    }

    public void resetAll() {
        this.mapPoint.keySet().forEach(point -> {
            this.resetPoint(point);
        });
    }

    @Transactional
    public synchronized Optional<MeansurementPointStatus> getPoint(String point) {

        if (this.mapPoint.containsKey(point)) {
            return Optional.ofNullable(this.mapPoint.get(point));
        } else if (Optional.ofNullable(point).isPresent()) {
            MeansurementPointStatus status = this.mapPoint.values().stream().findFirst().get();
            MeansurementPointStatus statusNew = new MeansurementPointStatus(point, status.getMonth(), status.getYear());
            statusNew = this.repository.save(statusNew);
            statusNew.addObserver(this);
            this.mapPoint.put(point, statusNew);
            return Optional.ofNullable(statusNew);
        } else {
            return Optional.empty();
        }
    }

    @Transactional
    public Page<MeansurementPointStatus> find(Specification specification, Pageable page) {
        return this.repository.findAll(specification, page);
    }

    @Transactional
    public List<PointStatusSummaryDTO> summary(Long month, Long year) {
        return this.repository.summary(month, year);
    }

    public void shutdown() {
        pool.shutdown();
    }

}
