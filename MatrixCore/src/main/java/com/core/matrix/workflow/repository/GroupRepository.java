package com.core.matrix.workflow.repository;

import com.core.matrix.workflow.model.GroupActiviti;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */
@Repository
public interface GroupRepository extends JpaRepository<GroupActiviti, String>, JpaSpecificationExecutor<GroupActiviti> { 
    
    
    @Query(value = "SELECT b.* FROM act_hi_identitylink a inner join act_id_group b on a.GROUP_ID_ = b.ID_ where  TASK_ID_ = :task and a.GROUP_ID_ is not null", nativeQuery = true )
    List<GroupActiviti> findByTaskId(@Param("task")String task);
}
