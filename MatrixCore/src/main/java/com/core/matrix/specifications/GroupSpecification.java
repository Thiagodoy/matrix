/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.specifications;

import com.core.matrix.workflow.model.GroupActiviti;
import com.core.matrix.workflow.model.GroupActiviti_;
import java.util.Optional;
import org.springframework.data.jpa.domain.Specification;

/**
 *
 * @author thiag
 */
public class GroupSpecification {

    public static Specification<GroupActiviti> name(String name) {
        
        
        if(Optional.ofNullable(name).isPresent()){
               return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.upper(root.get(GroupActiviti_.name)), "%" + name.toUpperCase() + "%"); 
        }else{
            return null;
        }
        
        
    }   

}
