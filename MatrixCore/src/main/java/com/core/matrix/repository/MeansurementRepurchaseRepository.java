/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.repository;

import com.core.matrix.model.MeansurementRepurchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

/**
 *
 * @author thiag
 */
@Repository
public interface MeansurementRepurchaseRepository extends JpaRepository<MeansurementRepurchase, Long>, JpaSpecificationExecutor<MeansurementRepurchase> {
    
    
    
    @Modifying
    void deleteByProcessInstanceId(String processInstanceId);
    
}
