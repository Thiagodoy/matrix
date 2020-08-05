/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.service;

import com.core.matrix.model.MeansurementRepurchase;
import com.core.matrix.repository.MeansurementRepurchaseRepository;
import com.core.matrix.specifications.MeansurementRepurchaseSpecification;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
public class MeansurementRepurchaseService extends com.core.matrix.service.Service<MeansurementRepurchase, MeansurementRepurchaseRepository> {

    public MeansurementRepurchaseService(MeansurementRepurchaseRepository repositoy) {
        super(repositoy);
    }

    @Transactional
    public Page find(Long id, Long meansurementFileId, String processIntanceId, Pageable page) {

        List<Specification> predicative = new ArrayList<>();

        if (Optional.ofNullable(id).isPresent()) {
            predicative.add(MeansurementRepurchaseSpecification.id(id));
        }

        if (Optional.ofNullable(meansurementFileId).isPresent()) {
            predicative.add(MeansurementRepurchaseSpecification.meansurementFileId(meansurementFileId));
        }

        if (Optional.ofNullable(processIntanceId).isPresent()) {
            predicative.add(MeansurementRepurchaseSpecification.processIntanceId(processIntanceId));
        }

        Specification<MeansurementRepurchase> spc = predicative.stream().reduce((a, b) -> a.and(b)).orElse(null);

        return this.repository.findAll(spc, page);

    }

    @Transactional(transactionManager = "matrixTransactionManager")
    public void deleteByProcessInstanceId(String processInstanceId) {

        this.find(null, null, processInstanceId, Pageable.unpaged()).getContent().stream().forEach(c -> {
            
            MeansurementRepurchase m = (MeansurementRepurchase)c;
            this.repository.deleteById(m.getId());
            
        });        
    }

}
