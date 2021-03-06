/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.repository;

import com.core.matrix.dto.ContractStatusSummaryDTO;
import com.core.matrix.model.ContractMtxStatus;
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
public interface ContractMtxStatusRepository extends JpaRepository<ContractMtxStatus, Long>, JpaSpecificationExecutor<ContractMtxStatus> {

    List<ContractMtxStatus> findByMonthAndYear(Long month, Long year);
    
    @Query(nativeQuery = true)
    List<ContractStatusSummaryDTO>summary(@Param("month")Long month,@Param("year")Long year);

}
