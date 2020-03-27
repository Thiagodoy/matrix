/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.specifications;

import com.core.matrix.model.Price;
import com.core.matrix.model.Price_;
import org.springframework.data.jpa.domain.Specification;

/**
 *
 * @author thiag
 */
public class PriceSpecification {

    public static Specification<Price> id(Long id) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get(Price_.id), id);
    }

    public static Specification<Price> subMarket(Long subMarket) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get(Price_.subMarket), subMarket);
    }

    public static Specification<Price> description(String description) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.upper(root.get(Price_.description)), "%" + description.toUpperCase() + "%");
    }

}
