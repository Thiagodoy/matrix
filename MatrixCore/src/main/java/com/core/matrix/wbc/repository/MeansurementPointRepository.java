/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.wbc.repository;

import com.core.matrix.wbc.model.MeansurementPoint;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author thiag
 */
@Repository
public interface MeansurementPointRepository extends JpaRepository<MeansurementPoint, Long> {   
    
    Optional<MeansurementPoint> findByCode(String code);
    Optional<MeansurementPoint> findByDescriptionPointStartingWith(String description);
}
