/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.request;

import javax.persistence.Column;
import lombok.Data;

/**
 *
 * @author thiag
 */
@Data
public class ContactManagerRequest {

    private Long id;
    private String name;
    private String email;
    private Long manager;
    private String telephone1;
    private String telephone2;
    private String telephone3;
    private String typeContact;

}
