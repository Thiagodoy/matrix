/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.specifications;

import com.core.matrix.model.Product;
import com.core.matrix.model.Product_;
import org.springframework.data.jpa.domain.Specification;

/**
 *
 * @author thiag
 */
public class ProductSpecification {

    public static Specification<Product> subMarket(Long code) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get(Product_.subMarket), code);
    }  

    public static Specification<Product> product(String name) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.upper(root.get(Product_.name)), "%" + name.toUpperCase() + "%");
    }
}
