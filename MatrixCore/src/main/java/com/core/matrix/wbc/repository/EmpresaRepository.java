/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.wbc.repository;

import com.core.matrix.wbc.dto.CompanyDTO;
import com.core.matrix.wbc.model.Empresa;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
public interface EmpresaRepository extends JpaRepository<Empresa, Long>, JpaSpecificationExecutor<Empresa> {
    Page<Empresa> findByNrCnpjStartingWith(String cnpj, Pageable page);
    Page<Empresa> findByEmpresaContaining(String razaoSocial, Pageable page);
    Page<Empresa> findByApelidoContaining(String apelido, Pageable page);
    
    @Query(nativeQuery = true)
    Optional<CompanyDTO> listByPoint(@Param("point")String point);

}
