/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.specifications;

import com.core.matrix.model.Manager;
import com.core.matrix.model.Manager_;
import org.springframework.data.jpa.domain.Specification;

/**
 *
 * @author thiag
 */
public class ManagerSpecification {

    public static Specification<Manager> companyName(String name){
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.upper(root.get(Manager_.companyName)), name + "%");
    }
    
    public static Specification<Manager> fancyName(String name){
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.upper(root.get(Manager_.fancyName)), name + "%");
    }


    
}
