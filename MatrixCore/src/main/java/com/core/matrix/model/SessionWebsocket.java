/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.model;

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
@Table(name = "mtx_session")
@Data
public class SessionWebsocket {
    
    @Id
    @Column(name = "sessao")
    private String sessionId;
    
    @Column(name = "usuario")
    private String userId;
    
    
}
