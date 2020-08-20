/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.repository;

import com.core.matrix.dto.MeansurementFileResultStatusDTO;
import com.core.matrix.model.MeansurementFileResult;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author thiag
 */
@Repository
public interface MeansurementFileResultRepository extends JpaRepository<MeansurementFileResult, Long> {
    
    
    List<MeansurementFileResult>findByIdProcess(String id);
    @Modifying
    void deleteByIdProcess(String id);
    
    @Query(nativeQuery = true)
    List<MeansurementFileResultStatusDTO> getStatusBilling(@Param("start")LocalDateTime start, @Param("end")LocalDateTime end);
    
    @Modifying
    @Query(value = "update MeansurementFileResult s set s.isExported = true where s.meansurementFileId = :id ")    
    void updateToExported(@Param("id")Long id);
    
    @Modifying
    @Query(value = "update MeansurementFileResult s set s.isExported = true where s.idProcess = :id ")    
    void updateToExportedByProcessInstance(@Param("id")String id);
    
    
    
    
    
}
