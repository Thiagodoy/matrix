/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.service;

import com.core.matrix.model.Product;
import com.core.matrix.repository.ProductRepository;
import com.core.matrix.specifications.ProductSpecification;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
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
public class ProductService {

    @Autowired
    private ProductRepository repository;

    @Transactional
    public void save(Product product) {
        this.repository.save(product);
    }

    @Transactional
    public void update(Product product) throws Exception {

        Product entiProduct = this.repository
                .findById(product.getId())
                .orElseThrow(() -> new Exception("Nenhum produto foi encontrado!"));

        entiProduct.update(product);
        this.repository.save(entiProduct);

    }

    @Transactional
    public void delete(Long id) {
        this.repository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Page find(Long subMarket, Long wbcCodigoPerfilCCEE, String wbcPerfilCCEE, String subMarketDescription, Pageable page) {

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

        return this.repository.findAll(spc, page);

    }

}
