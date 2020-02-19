/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.wbc.repository;

import com.core.matrix.wbc.model.MeansurementPoint;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author thiag
 */
@Repository
public interface MeansurementPointRepository extends JpaRepository<MeansurementPoint, Long> {   
    
    
    Optional<MeansurementPoint> findByCode(String code);
    Page<MeansurementPoint> findByCompany(Long comapny, Pageable page);
    Page<MeansurementPoint> findByCompanyAndCode(Long comapny,String code, Pageable page);
    Optional<MeansurementPoint> findByDescriptionPointStartingWith(String description);
    
    List<MeansurementPoint> listByCompanys(@Param("companys")List<Long>companys);
}
