/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.repository;

import com.core.matrix.model.Log;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author thiag
 */
@Repository
public interface LogRepository extends JpaRepository<Log, Long>, JpaSpecificationExecutor<Log>{
    
    
    void deleteByFileId(Long id);

    @Modifying
    @Query(value = "delete from Log l where l.processInstanceId = :idPro")
    void deleteBulk(@Param("idPro")String process);
    
    long deleteByprocessInstanceId(String id);
    
    
    Page<Log> findByProcessInstanceId(String process);
}
