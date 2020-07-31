/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.workflow.repository;

import com.core.matrix.workflow.model.CommentActiviti;
import javax.transaction.Transactional;
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
public interface CommentRepository extends JpaRepository<CommentActiviti, String> {
    
    
    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "update activiti.ACT_HI_COMMENT set user_id_ = :user where id_ = :id")
    public void update(@Param("id") String id, @Param("user")String user);
    
}
