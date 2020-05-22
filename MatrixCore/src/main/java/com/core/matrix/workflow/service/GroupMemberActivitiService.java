/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.workflow.service;

import com.core.matrix.workflow.model.GroupMemberActiviti;
import com.core.matrix.workflow.repository.GroupMemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author thiag
 */
@Service
public class GroupMemberActivitiService {

    @Autowired
    private GroupMemberRepository repository;

    @Transactional(transactionManager = "transactionManager")
    public void delete(GroupMemberActiviti.IdClass id) {
        this.repository.deleteById(id);
    }

}
