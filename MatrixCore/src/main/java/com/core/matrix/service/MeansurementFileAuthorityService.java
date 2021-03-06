/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.service;

import com.core.matrix.model.MeansurementFileAuthority;
import com.core.matrix.repository.MeansurementFileAuthorityRepository;
import com.core.matrix.specifications.MeansurementFileAuthoritySpecification;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author thiag
 */
@Service
public class MeansurementFileAuthorityService extends com.core.matrix.service.Service<MeansurementFileAuthority, MeansurementFileAuthorityRepository> {

    public MeansurementFileAuthorityService(MeansurementFileAuthorityRepository repositoy) {
        super(repositoy);
    }

    @Transactional(readOnly = true)
    public List<MeansurementFileAuthority> findByProcess(String processInstanceId) {
        return this.repository.findByProcessInstanceId(processInstanceId);
    }

    @Transactional(readOnly = true)
    public List<MeansurementFileAuthority> find(Long idMeansurementFile, String processIntanceId, String authority, String user, String userName) {

        if (Optional.ofNullable(idMeansurementFile).isPresent()) {
            MeansurementFileAuthority mfa = this.repository.findByIdMeansurementFile(idMeansurementFile);
            if (Optional.ofNullable(mfa).isPresent()) {
                return Arrays.asList(mfa);
            } else {
                return Collections.emptyList();
            }

        }

        List<Specification> specifications = new ArrayList();

        if (Optional.ofNullable(processIntanceId).isPresent()) {
            specifications.add(MeansurementFileAuthoritySpecification.processIntanceId(processIntanceId));
        }

        if (Optional.ofNullable(authority).isPresent()) {
            specifications.add(MeansurementFileAuthoritySpecification.authority(authority));
        }

        if (Optional.ofNullable(user).isPresent()) {
            specifications.add(MeansurementFileAuthoritySpecification.user(user));
        }

        if (Optional.ofNullable(userName).isPresent()) {
            specifications.add(MeansurementFileAuthoritySpecification.userName(userName));
        }

        Specification<MeansurementFileAuthority> spc = specifications.stream().reduce((a, b) -> a.and(b)).orElse(null);

        return this.repository.findAll(spc);

    }

    @Transactional(transactionManager = "matrixTransactionManager")
    public void deleteByProcessInstanceId(String id) {
        this.repository.deleteByProcessInstanceId(id);
    }

}
