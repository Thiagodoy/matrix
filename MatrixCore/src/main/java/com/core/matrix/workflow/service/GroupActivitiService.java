/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.workflow.service;

import com.core.matrix.workflow.model.GroupActiviti;
import com.core.matrix.workflow.repository.GroupRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author thiag
 */
@Service
public class GroupActivitiService {    
    
    @Autowired
    private GroupRepository repository;
    
    @Transactional
    public void save(GroupActiviti group){
        this.repository.save(group);
    }
    
    @Transactional
    public void delete(String id){
        this.repository.deleteById(id);
    }
    
    @Transactional(readOnly = true)
    public List<GroupActiviti>listAll(){
        return this.repository.findAll();
    }
    
    @Transactional(readOnly = true)
    public List<GroupActiviti>listAllCache(){
        return this.repository.findAll();
    }
    
    @Transactional(readOnly = true, transactionManager = "transactionManager")
    public List<GroupActiviti>listByTask(String id){
        return this.repository.findByTaskId(id);
    }
    
    
    
}
