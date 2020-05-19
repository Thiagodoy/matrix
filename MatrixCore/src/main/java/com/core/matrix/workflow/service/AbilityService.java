/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.workflow.service;

import com.core.matrix.workflow.model.AbilityActiviti;
import com.core.matrix.workflow.repository.AbilityRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author thiag
 */
@Service
public class AbilityService {    
    
    @Autowired
    private AbilityRepository repository;
    
    @Transactional(readOnly = true)
    public List<AbilityActiviti> findByGroup(String groupId){
        return this.repository.findByGroupId(groupId);
    }
    
}
