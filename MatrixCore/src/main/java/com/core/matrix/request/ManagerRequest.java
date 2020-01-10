/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.request;

import java.util.List;
import lombok.Data;

/**
 *
 * @author thiag
 */
@Data
public class ManagerRequest {

    private Long id;
    private String cnpj;
    private String companyName;
    private String fancyName;
    private String nickName;
    private List<ContactManagerRequest>contacts;
}
