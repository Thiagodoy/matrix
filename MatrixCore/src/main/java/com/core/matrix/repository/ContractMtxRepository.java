/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.repository;

import com.core.matrix.dto.ContractPointDTO;
import com.core.matrix.model.ContractMtx;
import java.util.List;
import java.util.Optional;
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
public interface ContractMtxRepository extends JpaRepository<ContractMtx, Long>, JpaSpecificationExecutor<ContractMtx> {

    Optional<ContractMtx> findByWbcContract(Long number);

    List<ContractMtx> findByCodeContractApportionment(Long number);

    Optional<ContractMtx> findByCodeWbcContract(Long number);
    
    @Query(nativeQuery = true)
    List<ContractPointDTO> associations(@Param("contracts") List<Long> contracts);

}
