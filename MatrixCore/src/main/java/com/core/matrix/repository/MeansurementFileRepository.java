/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.repository;

import com.core.matrix.dto.MeansurementFileStatusDTO;
import com.core.matrix.model.MeansurementFile;
import com.core.matrix.utils.MeansurementFileStatus;
import java.util.List;
import java.util.Optional;
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
public interface MeansurementFileRepository extends JpaRepository<MeansurementFile, Long> {

    @Modifying
    @Query(value = "update MeansurementFile c set c.status = :status where c.id = :id")
    void updateStatus(MeansurementFileStatus status, Long id);

    @Query(nativeQuery = true)
    List<MeansurementFileStatusDTO> getStatus(@Param("year") Long year, @Param("month") Long month);
    
    List<MeansurementFile>findByProcessInstanceId(String id);
    
    @Query(value = "select * from mtx_arquivo_de_medicao f where f.act_id_processo = :processo and f.status <> 'SUCCESS' ",nativeQuery = true)
    List<MeansurementFile>findAllFilesWithErrors(@Param("process")String processInstanceId);
    
    @Query(value = "select c from MeansurementFile c where c.wbcContract = :contract and c.meansurementPoint = :point and c.year = :year and c.month = :month")
    Optional<MeansurementFile> exists(@Param("contract")Long contract, @Param("point")String meansurementPoint, @Param("month")Long month, @Param("year") Long year);
    

}
