package com.core.matrix.workflow.model;


import java.util.Arrays;
import java.util.Collection;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Data;
import org.activiti.engine.identity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */




@Entity
@Table(schema = "activiti", name = "act_id_user")
@Data
public class UserActiviti implements UserDetails, User{

    @Id
    @Column(name = "ID_")
    private String id;

    @Column(name = "REV_")
    private Long rev;

    @Column(name = "FIRST_")
    private String firstName;

    @Column(name = "LAST_")
    private String lastName;

    @Column(name = "EMAIL_")
    private String email;

    @Column(name = "PWD_")
    private String password;

    @Column(name = "PICTURE_ID_")
    private String picture;

//    @Column(name = "USER_MASTER_ID_")
//    private String userMasterId;    
   

//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
//    @Column(name = "created_at_")
//    private LocalDateTime createdAt;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "userId", fetch = FetchType.EAGER)
    private Set<GroupMemberActiviti> groups;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "userId", fetch = FetchType.EAGER)
    private List<UserInfoActiviti> info;   

    public UserActiviti() {
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        //FIXME Corrigir 
        return Arrays.asList(new AbilityActiviti());
    }

    @Override
    public String getUsername() {
        return this.id;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    } 

    @Override
    public boolean isPictureSet() {
       return Optional.ofNullable(this.picture).isPresent();
    }

}
