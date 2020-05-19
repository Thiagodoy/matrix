/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.workflow.service;

import com.core.matrix.request.UserDeleteRequest;
import com.core.matrix.request.UserInfoRequest;
import com.core.matrix.response.UserInfoResponse;
import com.core.matrix.utils.Utils;
import com.core.matrix.workflow.model.GroupActiviti;
import com.core.matrix.workflow.model.GroupMemberActiviti;
import com.core.matrix.workflow.model.UserActiviti;
import com.core.matrix.workflow.repository.UserRepository;
import com.core.matrix.workflow.specification.UserActivitiSpecification;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
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
public class UserActivitiService {
    
    @Autowired
    private UserRepository repository;
    
    @Transactional
    public void save(UserActiviti user){        
        user.setPassword(Utils.encodePassword(user.getPassword())); 
        
        Optional<GroupMemberActiviti> opt =  user.getGroups().stream().findFirst();
        String profile = opt.isPresent() ? opt.get().getGroupId() : "without-profile";
        user.setProfile(profile);
        
        
        this.repository.save(user);
    }
    
    @Transactional
    public void delete(UserDeleteRequest request){
        this.repository.deleteById(request.getEmail());
    }
    
    
    @Transactional(readOnly = true) 
    public UserActiviti findById(String id) throws Exception{
        return this.repository.
                findById(id)
                .orElseThrow(()-> new Exception("User not found."));
    }
    
    public List<UserInfoResponse> getUserInfo(UserInfoRequest request){       
        return this.repository.findAllById(request.getUsers()).parallelStream().map( u -> new UserInfoResponse(u)).collect(Collectors.toList());
    }

    
    
    @Transactional(readOnly = true)
    public Page<UserActiviti>  list(Specification spc,  Pageable page){
        return repository.findAll(spc, page);
    }
    
    
    
    @Deprecated
    @Transactional(readOnly = true)
    public Page<UserActiviti>  list(String firstName, String lastName, String email, String profile,  Pageable page){
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
        
        if (Optional.ofNullable(profile).isPresent()) {
            predicates.add(UserActivitiSpecification.profile(profile));
        } 

        Specification<UserActiviti> specification = predicates.stream().reduce((a, b) -> a.and(b)).orElse(null);

        return repository.findAll(specification, page);
    }
    
    public GroupActiviti getProfile(){
        return null;
    }
    
}
