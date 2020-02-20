/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.repository;

import com.core.matrix.dto.ContractInformationDTO;
import com.core.matrix.model.ContractCompInformation;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author thiag
 */
@Repository
public interface ContractCompInformationRepository extends JpaRepository<ContractCompInformation, ContractCompInformation.IdClass> {
    
    
    @Query(nativeQuery = true)
    Optional<ContractInformationDTO> listByPoint(@Param("point") String point);
    
    Optional<ContractCompInformation> findByWbcContract(Long contract);
    List<ContractCompInformation> findByCodeContractApportionment(Long contract);
    
    
    Optional<ContractCompInformation> findByCodeWbcContract(Long contract);
    
    
    Optional<ContractCompInformation> findByWbcContractAndMeansurementPoint(Long contract, String point);
}
