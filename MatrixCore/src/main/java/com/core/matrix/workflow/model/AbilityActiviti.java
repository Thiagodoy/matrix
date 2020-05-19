/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.workflow.model;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.security.core.GrantedAuthority;

/**
 *
 * @author thiag
 */
@Data
@EqualsAndHashCode(of = {"subject","action",})
@Entity
@Table(name = "act_id_ability")
public class AbilityActiviti implements GrantedAuthority, Serializable {

    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_")
    private Long id;
    
    @Column(name = "subject_")
    private String subject;
    
    @Column(name = "action_")
    private String action;    
    
    @Column(name = "description_")
    private String description;    
    
    @Column(name = "group_id_", nullable = false)
    private String groupId;  
    
    
    public AbilityActiviti(String groupId){
        this.groupId = groupId;
    }
    
    public AbilityActiviti(){
        
    }
   
    
    @Override
    public String getAuthority() {
        return this.subject;
    }
    
}
