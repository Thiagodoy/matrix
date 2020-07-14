/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.wbc.repository;

import com.core.matrix.wbc.dto.ContractDTO;
import com.core.matrix.wbc.dto.ContractWbcInformationDTO;
import com.core.matrix.wbc.model.Contract;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author thiag
 */
@Repository
public interface ContractRepository extends JpaRepository<Contract, Long> {

    @Query(nativeQuery = true)
    Page<ContractDTO> shortInfomation(@Param("contractId") Long contractId, Pageable page);

    @Query(nativeQuery = true)
    List<ContractDTO> fullInformation(@Param("numeroDoContrato") Long contractId);

    @Query(nativeQuery = true, value = "SELECT COUNT(1) \n"
            + "  FROM [CE_CONTRATO] CT\n"
            + " WHERE CT.[nCdSituacaoContrato] in (2,8)\n"
            + "   AND CT.[sNrContrato] IN (SELECT CC.[nCdContrato]\n"
            + "  FROM [CE_CONTRATO_CLASSIFICADOR] CC,\n"
            + "       [CE_CLASSIFICADOR] CL\n"
            + "  WHERE CC.sCdClassificador = CL.sCdClassificador\n"
            + "    AND CL.[sNmClassificador] = 'PORTAL')")
    Long countContract();

    @Query(nativeQuery = true)
    List<ContractWbcInformationDTO> getInformation(@Param("year") Long year, @Param("month") Long month, @Param("contractId") List<Long> contractId);

    @Query(nativeQuery = true)
    List<ContractDTO> listForBilling();
}
