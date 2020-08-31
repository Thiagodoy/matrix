/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.service;

import com.core.matrix.dto.SubMarketDTO;
import com.core.matrix.model.Product;
import com.core.matrix.repository.ProductRepository;
import com.core.matrix.specifications.ProductSpecification;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
public class ProductService extends com.core.matrix.service.Service<Product, ProductRepository> {

    public ProductService(ProductRepository repositoy) {
        super(repositoy);
    }

    @Transactional(readOnly = true)
    public Page find(Long subMarket, Long wbcCodigoPerfilCCEE, String wbcPerfilCCEE, String subMarketDescription, Pageable page) throws Exception {

        Specification<Product> spc = ProductSpecification.find(subMarket, wbcCodigoPerfilCCEE, wbcPerfilCCEE, subMarketDescription);
        return this.find(spc, page);       

    }

    @CacheEvict(value = "submarkets",allEntries = true)
    @Override
    public void update(Product update) throws Exception {
        super.update(update); 
    }

    @CacheEvict(value = "submarkets",allEntries = true)
    @Override
    public List<Long> save(List<Product> entitys) {
        return super.save(entitys);
    }

    @CacheEvict(value = "submarkets",allEntries = true)
    @Override
    public Long save(Product entity) {
        return super.save(entity); 
    }

    @Cacheable(value = "submarkets")
    public List<SubMarketDTO> getAllSubMarkets() {
        return this.repository.findAll().stream().map(SubMarketDTO::new).distinct().collect(Collectors.toList());
    }

}
