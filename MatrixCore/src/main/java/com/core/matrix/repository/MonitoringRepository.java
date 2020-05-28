/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.repository;

import com.core.matrix.dto.MonitoringFilterDTO;
import com.core.matrix.dto.MonitoringStatusDTO;
import com.core.matrix.model.Monitoring;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author thiag
 */
@Repository
public interface MonitoringRepository extends JpaRepository<Monitoring, Double>, JpaSpecificationExecutor<Monitoring>{
    
    
    @Query(nativeQuery = true)
    List<MonitoringStatusDTO> status(@Param("mes")Long mes,@Param("ano")Long ano);
    
    @Query(nativeQuery = true)
    List<MonitoringFilterDTO>filters();
    
    
    
    
    
    
}
