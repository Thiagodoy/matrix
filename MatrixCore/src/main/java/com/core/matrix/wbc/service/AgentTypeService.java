/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.wbc.service;

import com.core.matrix.wbc.model.AgentType;
import com.core.matrix.wbc.repository.AgentTypeRepository;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 *
 * @author thiag
 */
@Service
public class AgentTypeService {    
    
    @Autowired
    private AgentTypeRepository repository;    
    
    @Cacheable("agentType")
    public List<AgentType>listAll(){        
        return this.repository.findAll();
    }
    
}
