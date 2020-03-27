/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.service;

import com.core.matrix.model.AuthorityApproval;
import com.core.matrix.repository.AuthorityApprovalRepository;
import com.core.matrix.specifications.AuthorityApprovalSpecification;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author thiag
 */
@Service
public class AuthorityApprovalService {

    @Autowired
    private AuthorityApprovalRepository repository;

    @Transactional
    public void save(AuthorityApproval request) {
        this.repository.save(request);
    }

    @Transactional
    public void update(AuthorityApproval request) throws Exception {

        AuthorityApproval approval = this.repository
                .findById(request.getId())
                .orElseThrow(() -> new Exception("Alçada não encontrada!"));

        approval.update(request);

        this.repository.save(approval);

    }

    @Transactional(readOnly = true)
    public AuthorityApproval findByAuthority(String value) throws Exception {
        return this.repository
                .findByAuthority(value)
                .orElseThrow(() -> new Exception("Alçada não encontrada! valor recebido -> " + value));
    }

    @Transactional(readOnly = true)
    public AuthorityApproval findBetween(Double value) throws Exception {
        
        Optional<AuthorityApproval> opt = this.repository
                .findValueBetween(value);

        if (opt.isPresent()) {
            return opt.get();
        } else {            
            return this.repository
                    .findAll()
                    .stream()
                    .sorted(Comparator.comparing(AuthorityApproval::getId).reversed())
                    .findFirst()
                    .orElseThrow(() -> new Exception("Alçada não encontrada! valor recebido -> " + value));
        }

    }

    @Transactional(readOnly = true)
    public Page find(Long id, String authority, Pageable page) {

        List<Specification> predicatives = new ArrayList<>();

        if (Optional.ofNullable(id).isPresent()) {
            predicatives.add(AuthorityApprovalSpecification.id(id));
        }

        if (Optional.ofNullable(authority).isPresent()) {
            predicatives.add(AuthorityApprovalSpecification.authority(authority));
        }

        Specification<AuthorityApproval> spc = predicatives.stream().reduce((a, b) -> a.and(b)).orElse(null);

        return this.repository.findAll(spc, page);
    }

    @Transactional
    public void delete(Long id) {
        this.repository.deleteById(id);
    }

}
