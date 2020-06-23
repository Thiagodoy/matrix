/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.repository;

import com.core.matrix.model.ProcessStatusLote;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author thiag
 */
@Repository
public interface ProcessStatusLoteRepository extends JpaRepository<ProcessStatusLote, Long> {

    Page<ProcessStatusLote> findByProcessInstanceIdLote(String process, Pageable page);
    List<ProcessStatusLote> findByProcessInstanceIdLote(String process);

}
