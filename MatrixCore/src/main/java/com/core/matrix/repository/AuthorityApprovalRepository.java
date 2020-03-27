/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.repository;

import com.core.matrix.model.AuthorityApproval;
import java.util.Optional;
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
public interface AuthorityApprovalRepository extends JpaRepository<AuthorityApproval, Long>, JpaSpecificationExecutor<AuthorityApproval> {

    Optional<AuthorityApproval> findByAuthority(String value);

    @Query(nativeQuery = true, value = "select * from mtx_alcada_aprovacao where :value between faixa_min and faixa_max")
    Optional<AuthorityApproval> findValueBetween(@Param("value") Double value);
}
