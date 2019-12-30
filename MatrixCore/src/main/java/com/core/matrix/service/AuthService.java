/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.service;

import com.core.matrix.request.AuthRequest;
import com.core.matrix.workflow.model.UserActiviti;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import com.core.matrix.workflow.repository.UserRRepository;

/**
 *
 * @author thiag
 */
@Service
public class AuthService implements UserDetailsService {
    
    
    @Autowired(required = true)
    private UserRRepository repository;
    
    
    public void auth(AuthRequest request){
        
    }   
   

    @Override
    public UserDetails loadUserByUsername(String string) throws UsernameNotFoundException {        
        return  this.repository.findById(string).orElseThrow(()-> new UsernameNotFoundException("User not found!") );
    }

    
}
