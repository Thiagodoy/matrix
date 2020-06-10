/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.specifications;

import com.core.matrix.model.Product;
import com.core.matrix.model.Product_;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.domain.Specification;

/**
 *
 * @author thiag
 */
public class ProductSpecification {

    
    public static Specification<Product> find(Long subMarket, Long wbcCodigoPerfilCCEE, String wbcPerfilCCEE, String subMarketDescription){
        List<Specification> predicates = new ArrayList<>();

        if (Optional.ofNullable(subMarket).isPresent()) {
            predicates.add(ProductSpecification.subMarket(subMarket));
        }

        if (Optional.ofNullable(wbcPerfilCCEE).isPresent()) {
            predicates.add(ProductSpecification.product(wbcPerfilCCEE));
        }

        if (Optional.ofNullable(wbcCodigoPerfilCCEE).isPresent()) {
            predicates.add(ProductSpecification.codigoPerfilCCEE(wbcCodigoPerfilCCEE));
        }
        
        if (Optional.ofNullable(subMarketDescription).isPresent()) {
            predicates.add(ProductSpecification.subMarketDescription(subMarketDescription));
        }
                
        Specification<Product> spc = predicates.stream().reduce((a, b) -> a.and(b)).orElse(null);

        return spc;
    }
    
    public static Specification<Product> subMarket(Long code) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get(Product_.subMarket), code);
    }  

    public static Specification<Product> product(String wbcPerfilCCEE) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.upper(root.get(Product_.wbcPerfilCCEE)), "%" + wbcPerfilCCEE.toUpperCase() + "%");
    }

    public static Specification<Product> codigoPerfilCCEE(Long wbcCodigoPerfilCCEE) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get(Product_.wbcCodigoPerfilCCEE), wbcCodigoPerfilCCEE);
    }     
    
    public static Specification<Product> subMarketDescription(String subMarketDescription) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get(Product_.subMarketDescription), subMarketDescription);
    } 
}
