package com.core.matrix.workflow.model;

import java.time.LocalDateTime;
import java.util.Optional;
import javax.annotation.PostConstruct;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */
@Entity
@Table(schema = "activiti", name = "act_id_info")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"userId", "key", "value"})
public class UserInfoActiviti implements Model<UserInfoActiviti>{

    @Id
    @Column(name = "ID_")
    public String id;

    @Column(name = "REV_")
    public Long rev;

    @Column(name = "USER_ID_")
    public String userId;

    @Column(name = "TYPE_")
    public String type;

    @Column(name = "KEY_")
    public String key;

    @Column(name = "VALUE_")
    public String value;   
    

    public UserInfoActiviti(String userId, String key, String value) {
        this.rev = 1L;
        this.type = "userinfo";
        this.key = key;
        this.value = value;
        this.userId = userId;
    }

    @PostConstruct
    @PrePersist
    public void generateId() {
        
        if(!Optional.ofNullable(this.id).isPresent()){
            this.id = String.valueOf(hashCode() + LocalDateTime.now().hashCode());
        }
    }
}
