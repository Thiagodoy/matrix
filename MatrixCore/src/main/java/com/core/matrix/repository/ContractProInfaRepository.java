/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.repository;

import com.core.matrix.model.ContractProInfa;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author thiag
 */
public interface ContractProInfaRepository extends JpaRepository<ContractProInfa, ContractProInfa.IdClass>{
    
}
