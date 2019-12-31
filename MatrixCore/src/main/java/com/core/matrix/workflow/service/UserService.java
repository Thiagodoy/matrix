/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.workflow.service;

import com.core.matrix.request.UserDeleteRequest;
import com.core.matrix.utils.Utils;
import com.core.matrix.workflow.model.UserActiviti;
import com.core.matrix.workflow.repository.UserRepository;
import com.core.matrix.workflow.specification.UserActivitiSpecification;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

/**
 *
 * @author thiag
 */
@Service
public class UserService {
    
    @Autowired
    private UserRepository repository;
    
    public void save(UserActiviti user){        
        user.setPassword(Utils.encodePassword(user.getPassword()));        
        this.repository.save(user);
    }
    
    public void delete(UserDeleteRequest request){
        this.repository.deleteById(request.getEmail());
    }
    
    public Page<UserActiviti>  list(String firstName, String lastName, String email,  Pageable page){
         List<Specification<UserActiviti>> predicates = new ArrayList<>();

        if (Optional.ofNullable(firstName).isPresent()) {
            predicates.add(UserActivitiSpecification.firstName(firstName));
        }
        
        if (Optional.ofNullable(lastName).isPresent()) {
            predicates.add(UserActivitiSpecification.lastName(lastName));
        }
        
        if (Optional.ofNullable(email).isPresent()) {
            predicates.add(UserActivitiSpecification.email(email));
        }    

        Specification<UserActiviti> specification = predicates.stream().reduce((a, b) -> a.and(b)).orElse(null);

        return repository.findAll(specification, page);
    }
    
    
}
