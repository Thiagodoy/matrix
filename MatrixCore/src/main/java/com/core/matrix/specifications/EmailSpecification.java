/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.specifications;

import com.core.matrix.model.Email;
import com.core.matrix.model.Email_;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.domain.Specification;

/**
 *
 * @author thiag
 */
public class EmailSpecification {

    public static Specification<Email> find(Long id, String data, Long error, Long retry, Email.EmailStatus status) {

        List<Specification> predicatives = new ArrayList<>();

        if (Optional.ofNullable(id).isPresent()) {
            predicatives.add((root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get(Email_.id), id));
        }

        if (Optional.ofNullable(data).isPresent()) {
            predicatives.add((root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.upper(root.get(Email_.data)), "%" + data.toUpperCase() + "%"));
        }
        
        if (Optional.ofNullable(error).isPresent()) {
            predicatives.add((root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get(Email_.error), error));
        }
        
        if (Optional.ofNullable(status).isPresent()) {
            predicatives.add((root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get(Email_.status), status));
        }
        
        if (Optional.ofNullable(status).isPresent()) {
            predicatives.add((root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get(Email_.status), status));
        }

        Specification<Email> spc = predicatives.stream().reduce((a, b) -> a.and(b)).orElse(null);

        return spc;
    }
    
    public static Specification<Email> createAt(LocalDateTime date){
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.lessThan(root.get(Email_.createdAt), date);
    }

}
