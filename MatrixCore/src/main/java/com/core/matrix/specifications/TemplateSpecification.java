/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.specifications;

import com.core.matrix.model.Template;
import com.core.matrix.model.Template_;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.domain.Specification;

/**
 *
 * @author thiag
 */
public class TemplateSpecification {

    public static Specification<Template> filter(Long id, String subject, Long version, Template.TemplateBusiness business) {
        
        List<Specification> predicative = new ArrayList<>();
        
        if(Optional.ofNullable(id).isPresent()){            
            predicative.add((root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get(Template_.id), id));
        }
        
        if(Optional.ofNullable(subject).isPresent()){            
            predicative.add((root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.upper(root.get(Template_.subject)), "%" + subject.toUpperCase() + "%"));
        }
        
        if(Optional.ofNullable(version).isPresent()){            
            predicative.add((root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get(Template_.version), version));
        }
        
        if(Optional.ofNullable(business).isPresent()){            
            predicative.add((root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get(Template_.business), business));
        }
        
        
        Specification spc = predicative.stream().reduce((a,b)-> a.and(b)).orElse(null);
        
        return spc;
        
    }  

}
