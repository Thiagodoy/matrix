/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.repository;

import com.core.matrix.model.MeansurementPointMtx;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 *
 * @author thiag
 */
@Repository
public interface MeansurementPointMtxRepository extends JpaRepository<MeansurementPointMtx, Long>, JpaSpecificationExecutor<MeansurementPointMtx> {

    Optional<MeansurementPointMtx> findByPoint(String point);
    
    List<MeansurementPointMtx> findByPointContaining(String point);
    
    @Query(value = "select wbc_ponto_de_medicao from mtx_ponto_de_medicao", nativeQuery = true) 
    List<String> findAllPoints();

}
