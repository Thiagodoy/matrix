/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.service;

import com.core.matrix.workflow.model.UserActiviti;
import java.util.List;
import java.util.Optional;
import org.activiti.engine.IdentityService;
import org.activiti.engine.identity.User;
import org.activiti.engine.identity.UserQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author thiag
 */
@Service
public class UserActivitiService {
    
    
    @Autowired
    private IdentityService identityService;
    
    public void save(UserActiviti user){
        this.identityService.saveUser(user);
    }
    
    public List<User> list(String firstName, String email, String group, int page, int size ){
    
        UserQuery query = this.identityService.createUserQuery();
        
        
        if(Optional.ofNullable(group).isPresent()){            
           query = query.memberOfGroup(group);            
        }
        
        if(Optional.ofNullable(firstName).isPresent()){
            query = query.userFullNameLike(firstName);
        }
        
        if(Optional.ofNullable(email).isPresent()){
            query = query.userEmailLike(email);
        }
        
        
        query = query.orderByUserFirstName();
        
         return  (page == 0 && size == 0) ? query.list() : query.listPage(page, size);
    
    }
    
    
}
