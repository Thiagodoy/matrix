/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.repository;

import com.core.matrix.dto.MeansurementFileStatusDTO;
import com.core.matrix.model.MeansurementFile;
import com.core.matrix.utils.MeansurementFileStatus;
import com.core.matrix.utils.MeansurementFileType;
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
    void updateStatus(@Param("status") MeansurementFileStatus status, @Param("id") Long id);
    
    @Modifying
    @Query(value = "update MeansurementFile c set c.file = :file where c.id = :id")
    void updateFile(@Param("file") String file, @Param("id") Long id);
    
    @Modifying
    @Query(value = "update MeansurementFile c set c.type = :type where c.id = :id")
    void updateType(@Param("type") MeansurementFileType type, @Param("id") Long id);

    @Query(nativeQuery = true)
    List<MeansurementFileStatusDTO> getStatus(@Param("year") Long year, @Param("month") Long month);
    
    List<MeansurementFile>findByProcessInstanceId(String id);
    
    @Query(value = "select * from mtx_arquivo_de_medicao f where f.act_id_processo = :processo and f.status <> 'SUCCESS' ",nativeQuery = true)
    List<MeansurementFile>findAllFilesWithErrors(@Param("process")String processInstanceId);
    
    @Query(value = "select * from mtx_arquivo_de_medicao f where f.status <> 'APPROVED' and f.year = :year and f.month = :month",nativeQuery = true)
    List<MeansurementFile>hasFilePending(@Param("year")Long year,@Param("month")Long month);
    
    @Query(value = "select c from MeansurementFile c where c.wbcContract = :contract and c.meansurementPoint = :point and c.year = :year and c.month = :month")
    Optional<MeansurementFile> exists(@Param("contract")Long contract, @Param("point")String meansurementPoint, @Param("month")Long month, @Param("year") Long year);
    

    Optional<MeansurementFile> findByWbcContractAndMeansurementPointAndMonthAndYear(Long contract, String point, Long month, Long year);
    
}
