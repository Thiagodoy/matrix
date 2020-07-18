/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.repository;

import com.core.matrix.model.MeansurementFileDetail;
import com.core.matrix.utils.MeansurementFileDetailStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

/**
 *
 * @author thiag
 */
@Repository
public interface MeansurementFileDetailRepository extends JpaRepository<MeansurementFileDetail, Long> {

    List<MeansurementFileDetail> findByIdMeansurementFile(Long id);
    
    List<MeansurementFileDetail> findByIdMeansurementFileAndStatusIn(Long id, List<MeansurementFileDetailStatus> status );
    
    @Modifying
    void deleteByIdMeansurementFile(Long id);
    
    @Modifying
    void deleteByIdMeansurementFileIn(List<Long> ids);
    
    
    

}
