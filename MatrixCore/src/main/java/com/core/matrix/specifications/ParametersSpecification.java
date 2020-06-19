/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.specifications;

import com.core.matrix.model.Parameters;
import com.core.matrix.model.Parameters_;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.domain.Specification;

/**
 *
 * @author thiag
 */
public class ParametersSpecification {

    public static Specification<Parameters> find(String key, String value, String description, String type, Boolean isApplication) {

        List<Specification> predicatives = new ArrayList();

        
        if (Optional.ofNullable(isApplication).isPresent()) {
            predicatives.add((root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get(Parameters_.isApplication), isApplication));
        }        
        
        if (Optional.ofNullable(key).isPresent()) {
            predicatives.add((root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(criteriaBuilder.upper(root.get(Parameters_.key)), key.toUpperCase()));
        }

        if (Optional.ofNullable(value).isPresent()) {
            predicatives.add((root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.upper(root.get(Parameters_.value)), "%" + value.toUpperCase() + "%"));
        }

        if (Optional.ofNullable(description).isPresent()) {
            predicatives.add((root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.upper(root.get(Parameters_.description)), "%" + description.toUpperCase() + "%"));
        }

        if (Optional.ofNullable(type).isPresent()) {

            Parameters.ParameterType enumType = Parameters.ParameterType.valueOf(type);

            if (Optional.ofNullable(enumType).isPresent()) {
                predicatives.add((root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get(Parameters_.type), enumType));
            }
        }

        Specification<Parameters> spc = predicatives.stream().reduce((a, b) -> a.and(b)).orElse(null);
        
        return spc;

    }

}
