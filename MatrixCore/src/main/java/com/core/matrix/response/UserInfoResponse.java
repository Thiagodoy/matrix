/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.response;

import com.core.matrix.workflow.model.UserActiviti;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author thiag
 */
@Data
@NoArgsConstructor
public class UserInfoResponse {

    private String email;
    private String firstName;
    private String lastName;
    
    public UserInfoResponse(UserActiviti user){
        this.email = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
    }

}
