/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.service;

import com.core.matrix.model.Manager;
import com.core.matrix.repository.ManagerRepository;
import com.core.matrix.request.ManagerRequest;
import com.core.matrix.specifications.ManagerSpecification;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author thiag
 */
@Service
public class ManagerService {

    @Autowired
    private ManagerRepository repository;

    @Transactional
    public void save(ManagerRequest request) {

        Manager manager = new Manager(request);
        this.repository.save(manager);
    }

    @Transactional
    public void update(ManagerRequest request) throws Exception {

        Manager entity = this.repository.findById(request.getId()).orElseThrow(() -> new Exception("Not found manager! "));

        if (Optional.ofNullable(request.getCompanyName()).isPresent() && !request.getCompanyName().equals(entity.getCompanyName())) {
            entity.setCompanyName(request.getCompanyName());
        }

        if (Optional.ofNullable(request.getFancyName()).isPresent() && !request.getFancyName().equals(entity.getFancyName())) {
            entity.setFancyName(request.getFancyName());
        }

        if (Optional.ofNullable(request.getNickName()).isPresent() && !request.getNickName().equals(entity.getNickName())) {
            entity.setNickName(request.getNickName());
        }

        this.repository.save(entity);

    }

    @Transactional
    public void delete(Long id) {
        this.repository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Manager findById(Long id) throws Exception {
        return this.repository
                .findById(id)
                .orElseThrow(() -> new Exception("Not found manager!"));
    }

    @Transactional(readOnly = true)
    public Page<Manager> find(String companyName, String cnpj, PageRequest page) {

        List<Specification> specifications = new ArrayList();

        if (Optional.ofNullable(companyName).isPresent()) {
            specifications.add(ManagerSpecification.companyName(companyName));
        }

        if (Optional.ofNullable(cnpj).isPresent()) {
            specifications.add(ManagerSpecification.cnpj(cnpj));
        }

        Specification<Manager> spc = specifications.stream().reduce((a, b) -> a.and(b)).orElse(null);

        return this.repository.findAll(spc, page);
    }

}
