/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.wbc.service;

import com.core.matrix.wbc.dto.ProdutoDTO;
import com.core.matrix.wbc.repository.ProdutoRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author thiag
 */
@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository repository;
   
    @Transactional(readOnly = true)
    public List<ProdutoDTO>list(){
       return this.repository.list();
    }
}
