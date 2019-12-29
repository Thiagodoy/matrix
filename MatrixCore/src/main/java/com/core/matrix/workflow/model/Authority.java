/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.workflow.model;

import org.springframework.security.core.GrantedAuthority;

/**
 *
 * @author thiag
 */
public class Authority implements GrantedAuthority {

    @Override
    public String getAuthority() {
        return "Teste";
    }
    
}
