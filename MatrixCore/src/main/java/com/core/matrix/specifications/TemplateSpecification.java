/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.specifications;

import com.core.matrix.model.Template;
import com.core.matrix.model.Template_;
import java.time.LocalDateTime;
import org.springframework.data.jpa.domain.Specification;

/**
 *
 * @author thiag
 */
public class TemplateSpecification {

    public static Specification<Template> filter(Long id, String subject, int version) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get(Template_.id), id);
    }  
    
    
   
    protected Long id;
    
   
    protected String template;
    
  
    protected String subject;    
    
 
    protected Long version;
    
   
    protected LocalDateTime createdAt;
    
   
    protected String parameters;

}
