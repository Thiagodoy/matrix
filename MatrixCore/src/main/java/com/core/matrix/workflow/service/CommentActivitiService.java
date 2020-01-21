/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.workflow.service;

import com.core.matrix.workflow.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author thiag
 */

@Service
public class CommentActivitiService {
    
    
    @Autowired
    private CommentRepository repository;
    
    @Transactional
    public void setUser(String id, String user){
        this.repository.update(id, user);
    }
    
}
