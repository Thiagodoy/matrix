/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.repository;

import com.core.matrix.dto.ContractUnBillingDTO;
import com.core.matrix.dto.FileStatusDTO;
import com.core.matrix.model.ProcessStatusLote;
import com.core.matrix.dto.MeansurementFileStatusDTO;
import com.core.matrix.model.MeansurementFile;
import com.core.matrix.utils.MeansurementFileStatus;
import com.core.matrix.utils.MeansurementFileType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    @Query(value = "update MeansurementFile c set c.status = :status where c.processInstanceId = :id")
    void updateStatusByProcessInstanceId(@Param("status") MeansurementFileStatus status, @Param("id") String id);
    
    @Modifying
    @Query(value = "update MeansurementFile c set c.file = null, c.status = :status where c.processInstanceId = :id")
    void updateStatusAndFileByProcessInstanceId(@Param("status") MeansurementFileStatus status, @Param("id") String id);

    @Modifying
    @Query(value = "update MeansurementFile c set c.file = :file where c.id = :id")
    void updateFile(@Param("file") String file, @Param("id") Long id);

    @Modifying
    @Query(value = "update MeansurementFile c set c.type = :type where c.id = :id")
    void updateType(@Param("type") MeansurementFileType type, @Param("id") Long id);

    @Modifying
    @Query(value = "delete MeansurementFile c where c.processInstanceId = :id")
    void deleteByProcessInstanceId(@Param("id") String id);

    @Query(nativeQuery = true)
    List<MeansurementFileStatusDTO> getStatus(@Param("year") Long year, @Param("month") Long month);

    @Query(nativeQuery = true)
    List<FileStatusDTO> getStatusBilling(@Param("process") List<String> process);

    Page<MeansurementFile> findByMonthAndYearAndStatusNotIn(Long month, Long year, List<MeansurementFileStatus> status, Pageable page);

    @Query(nativeQuery = true)
    List<ProcessStatusLote> findByProcessInstanceIdIn(@Param("process") List<String> process);

    
    List<MeansurementFile> findByProcessInstanceId(String id);
    
    @Query(value = "select e from MeansurementFile e left join fetch e.details b where e.processInstanceId = :id")
    List<MeansurementFile> findByProcessInstanceId2(String id);

    @Query(value = "select * from mtx_arquivo_de_medicao f where f.act_id_processo = :processo and f.status <> 'SUCCESS' ", nativeQuery = true)
    List<MeansurementFile> findAllFilesWithErrors(@Param("process") String processInstanceId);

    @Query(value = "select * from mtx_arquivo_de_medicao f where f.status <> 'APPROVED' and f.ano = :year and f.mes = :month", nativeQuery = true)
    List<MeansurementFile> hasFilePending(@Param("year") Long year, @Param("month") Long month);

    @Query(value = "select c from MeansurementFile c where c.wbcContract = :contract and c.meansurementPoint = :point and c.year = :year and c.month = :month")
    List<MeansurementFile> exists(@Param("contract") Long contract, @Param("point") String meansurementPoint, @Param("month") Long month, @Param("year") Long year);
    
    @Query(value = "select c from MeansurementFile c where c.wbcContract = :contract and c.year = :year and c.month = :month")
    List<MeansurementFile> exists(@Param("contract") Long contract, @Param("month") Long month, @Param("year") Long year);

    @Query(value = "select c from MeansurementFile c where c.wbcContract in :contract and c.year = :year and c.month = :month")
    List<MeansurementFile> exists(@Param("contract") List<Long> contract, @Param("month") Long month, @Param("year") Long year);

    List<ContractUnBillingDTO> contractUnbilling(@Param("contracts") List<Long> contract, @Param("month") Long month, @Param("year") Long year);

    List<MeansurementFile> findByWbcContractAndMeansurementPointAndMonthAndYear(Long contract, String point, Long month, Long year);

    @Query(nativeQuery = true, value = "SELECT \n"
            + "    (case when count(c.act_id_processo) > 0 then 'true' else 'false' end) as as_billing \n"
            + "FROM\n"
            + "    mtx_contrato_informacao_complementar a\n"
            + "        INNER JOIN\n"
            + "    mtx_contrato_informacao_complementar b ON a.wbc_codigo_contrato = b.wbc_codigo_contrato_rateio\n"
            + "        AND a.wbc_contrato = :contract\n"
            + "        LEFT JOIN \n"
            + "        mtx_arquivo_de_medicao c on b.wbc_contrato = c.wbc_contrato and b.wbc_ponto_de_medicao = c.wbc_ponto_de_medicao and c.ano = :year and c.mes = :month")
    boolean contractHasBilling(@Param("contract") Long contract, @Param("month") Long month, @Param("year") Long year);

    List<MeansurementFile> findByMonthAndYearAndWbcContractIn(Long month, Long year, List<Long> contracts);

    @Query(nativeQuery = true, value = "select id_arquivo_de_medicao from mtx_arquivo_de_medicao where act_id_processo = :id")
    List<Long> listIdsByProcessInstanceId(@Param("id") String id);

}
