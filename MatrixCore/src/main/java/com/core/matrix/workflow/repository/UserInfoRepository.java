package com.core.matrix.workflow.repository;

import com.core.matrix.workflow.model.UserInfoActiviti;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */
@Repository
public interface UserInfoRepository extends JpaRepository<UserInfoActiviti, String>, JpaSpecificationExecutor<UserInfoActiviti> { 
    
    
    
}
