/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.wbc.repository;

import com.core.matrix.wbc.dto.SubMercadoDTO;
import com.core.matrix.wbc.model.SubMercado;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 *
 * @author thiag
 */
@Repository
public interface SubMercadoRepository extends JpaRepository<SubMercado, Long>, JpaSpecificationExecutor<SubMercado> {
  
    @Query(nativeQuery = true)
    List<SubMercadoDTO> list();

}
