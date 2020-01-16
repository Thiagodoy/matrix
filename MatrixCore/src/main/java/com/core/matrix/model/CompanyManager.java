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
import javax.persistence.IdClass;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author thiag
 */
@Entity
@Table(name = "mtx_empresa_gestor")
@Data
@IdClass(CompanyManager.IdClass.class)
public class CompanyManager implements  Serializable{
    
    @Id
    @Column(name = "wbc_empresa")
    private Long empresa;
    @Id
    @Column(name = "id_gestor")
    private Long manager;
    
    @Data
    public static class IdClass implements Serializable {
        private Long empresa;
        private Long manager;
    }
    
}
