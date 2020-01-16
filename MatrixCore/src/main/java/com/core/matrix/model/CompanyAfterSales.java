/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author thiag
 */
@Entity
@Table(name = "mtx_empresa_posvenda")
@Data
public class CompanyAfterSales {
    
    @Id
    @Column(name = "wbc_empresa")
    private Long company;
    
    @Id
    @Column(name = "act_usuario")
    private String user; 
    
    
    @Data
    public static class IdClass implements Serializable {
        private Long company;
        private String user;
    }
    
    
}
