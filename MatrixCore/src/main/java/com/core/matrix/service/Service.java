/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.service;

import com.core.matrix.model.Model;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Data;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author thiag
 */
@Data
public abstract class Service<T extends Model, R extends JpaRepository<T, Long> & JpaSpecificationExecutor<T>> {

    protected R repository;

    public Service(R repositoy) {
        this.repository = repositoy;
    }

    @Transactional
    public Long save(T entity) {
        return this.repository.save(entity).getId();
    }

    @Transactional
    public List<Long> save(List<T> entitys) {
        return this.repository
                .saveAll(entitys)
                .stream()
                .map(T::getId)
                .collect(Collectors.toList());
    }

    @Transactional
    public void update(T update) throws Exception {

        T entity = this.repository.findById(update.getId()).orElseThrow(() -> new Exception("Not found entity"));
        
        entity.update(update);

        this.repository.save(entity);
    }

    @Transactional(readOnly = false)
    public Page find(Specification<T> spc, Pageable page) throws Exception {
        return this.repository.findAll(spc, page);
    }

    @Transactional(readOnly = true)
    public T find(Long id) throws Exception {
        return this.repository.findById(id).orElseThrow(() -> new Exception("Not found entity"));
    }

    @Transactional
    public void delete(Long id) throws Exception {
        this.repository.deleteById(id);
    }

}
