/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.service;

import com.core.matrix.dto.MonitoringContractDTO;
import com.core.matrix.model.MonitoringPoint;
import com.core.matrix.repository.MonitoringPointRepository;
import java.util.List;

/**
 *
 * @author thiag
 */
@org.springframework.stereotype.Service
public class MonitoringPointService extends Service<MonitoringPoint, MonitoringPointRepository> {  

    public MonitoringPointService(MonitoringPointRepository repositoy) {
        super(repositoy);
    }
    
    
    public List<MonitoringContractDTO>getStatusByContract(Long month, Long year){
        return this.repository.getStatusByContract(month, year);
    }
    
}
