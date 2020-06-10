/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.service;

import com.core.matrix.model.Price;
import com.core.matrix.repository.PriceRepository;
import com.core.matrix.specifications.PriceSpecification;
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
public class PriceService extends com.core.matrix.service.Service<Price, PriceRepository>{
    

    public PriceService(PriceRepository repositoy) {
        super(repositoy);
    }
    
    @Transactional
    public Page find(Long id, Long subMarket, String description, Pageable page) {

        List<Specification> predicatives = new ArrayList<>();

        if (Optional.ofNullable(id).isPresent()) {
            predicatives.add(PriceSpecification.id(id));
        }

        if (Optional.ofNullable(subMarket).isPresent()) {
            predicatives.add(PriceSpecification.subMarket(subMarket));
        }

        if (Optional.ofNullable(description).isPresent()) {
            predicatives.add(PriceSpecification.description(description));
        }

        Specification<Price> spc = predicatives.stream().reduce((a, b) -> a.and(b)).orElse(null);

        return this.repository.findAll(spc, page);

    }  

}
