package com.core.matrix.workflow.model;

import java.io.Serializable;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;

import javax.persistence.Entity;
import javax.persistence.Id;

import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(schema = "activiti", name = "act_id_membership")
@IdClass(GroupMemberActiviti.IdClass.class)
public class GroupMemberActiviti implements Serializable {

    private static final long serialVersionUID = -2636531586488935713L;

    @Id
    @Column(name = "USER_ID_")
    private String userId;

    @Id
    @Column(name = "GROUP_ID_")
    private String groupId;    
    
    
    @Transient
    private List<AbilityActiviti> abilitys;
    

    @Data
    public static class IdClass implements Serializable {

        private static final long serialVersionUID = 5316141980584556876L;
        private String userId;
        private String groupId;
    }
}
