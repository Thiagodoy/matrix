package com.core.matrix.workflow.repository;

import com.core.matrix.workflow.model.AbilityActiviti;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */
@Repository
public interface AbilityRepository extends JpaRepository<AbilityActiviti, Long>, JpaSpecificationExecutor<AbilityActiviti> {

    List<AbilityActiviti> findByGroupId(String groupId);

}
