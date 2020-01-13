/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.wbc.service;

import com.core.matrix.wbc.repository.EmpresaRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

/**
 *
 * @author thiag
 */
@Service
public class EmpresaService {

    @Autowired
    private EmpresaRepository repository;

    public Page findAll(String cnpj, String razaoSocial, PageRequest page) {

        if (Optional.ofNullable(cnpj).isPresent()) {
            return this.repository.findBySNrCnpjStartingWith(cnpj, page);
        } else if (Optional.ofNullable(razaoSocial).isPresent()) {
            return this.repository.findBySNmEmpresaStartingWith(razaoSocial, page);
        }

        return this.repository.findAll(page);
    }
    
    

}
