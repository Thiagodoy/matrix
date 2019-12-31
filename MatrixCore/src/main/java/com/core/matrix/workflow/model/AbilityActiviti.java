/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.workflow.model;

import com.google.common.base.Optional;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PrePersist;
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
    @Column(name = "id_")
    private String id;
    
    @Column(name = "subject_")
    private String subject;
    
    @Column(name = "action_")
    private String action;    
    
    @Column(name = "group_id_")
    private String groupId;  
    
    
    @PrePersist
    public void generate(){
        if(!Optional.fromNullable(id).isPresent()){
            this.id = String.valueOf(this.hashCode());
        }
    }
    
    @Override
    public String getAuthority() {
        return this.subject;
    }
    
}
