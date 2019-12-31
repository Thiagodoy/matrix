package com.core.matrix.workflow.model;

import java.io.Serializable;
import javax.persistence.Column;

import javax.persistence.Entity;
import javax.persistence.Id;

import javax.persistence.IdClass;
import javax.persistence.Table;

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
    
    @Id
    @Column(name = "USER_ID_")
    private String userId;
    
    @Id
    @Column(name = "GROUP_ID_")
    private String groupId;
    
    @Data
    public static class IdClass implements Serializable {
        private String userId;
        private String groupId;
    }
}

    