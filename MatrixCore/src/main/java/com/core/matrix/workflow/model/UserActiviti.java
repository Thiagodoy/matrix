package com.core.matrix.workflow.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
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
@JsonIgnoreProperties(value = {"authorities"})
public class UserActiviti implements UserDetails, User {

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

    @Column(name = "PROFILE_ID_")
    private String profile;

    @Column(name = "IS_ENABLED_")
    private boolean isEnabled;
    
    @Column(name = "RECEIVE_EMAIL_")
    private boolean isReceiveEmail;
    
    @Column(name = "RECEIVE_NOTIFICATION_")
    private boolean isReceiveNotification;

    @OneToMany(cascade = {CascadeType.REMOVE,CascadeType.PERSIST}, fetch = FetchType.EAGER)
    @JoinColumn(name = "USER_ID_")
    private Set<GroupMemberActiviti> groups;

    @OneToMany(cascade = {CascadeType.REMOVE,CascadeType.PERSIST}, fetch = FetchType.EAGER)
    @JoinColumn(name = "USER_ID_")
    private List<UserInfoActiviti> info;

    @PrePersist
    public void setValues() {
        this.isEnabled = true;
    }

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
        return this.isEnabled;
    }

    @Override
    public boolean isPictureSet() {

        return Optional.ofNullable(this.picture).isPresent();

    }

}
