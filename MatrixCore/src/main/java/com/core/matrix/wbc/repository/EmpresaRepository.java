/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.wbc.repository;

import com.core.matrix.wbc.model.Empresa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 *
 * @author thiag
 */
@Repository
public interface EmpresaRepository extends JpaRepository<Empresa, Long>, JpaSpecificationExecutor<Empresa> {
 
    
    
    Page<Empresa> findBySNrCnpj(String cnpj, PageRequest page);
    Page<Empresa> findBySNmEmpresa(String razaoSocial, PageRequest page);
    
}
