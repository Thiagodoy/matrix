package com.core.matrix.workflow.service;


import com.core.matrix.workflow.model.UserActiviti;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */
@Repository
public interface UserRepository extends JpaRepository<UserActiviti, String>, JpaSpecificationExecutor<UserActiviti> {    
      
    
}
