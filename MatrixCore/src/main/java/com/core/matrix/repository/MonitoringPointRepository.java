/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.repository;

import com.core.matrix.dto.MonitoringContractDTO;
import com.core.matrix.model.MonitoringPoint;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
public interface MonitoringPointRepository extends JpaRepository<MonitoringPoint, Long>,JpaSpecificationExecutor<MonitoringPoint>{
    
    
    
    @Query(nativeQuery = true)
    List<MonitoringContractDTO>getStatusByContract(@Param("month") Long month, @Param("year") Long year);        
    
}
