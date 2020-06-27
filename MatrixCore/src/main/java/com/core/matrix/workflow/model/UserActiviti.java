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
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
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
@EqualsAndHashCode(of = "id")
public class UserActiviti implements UserDetails, User, Model<UserActiviti> {

    @Id
    @Column(name = "ID_")
    protected String id;

    @Column(name = "REV_")
    protected Long rev;

    @Column(name = "FIRST_")
    protected String firstName;

    @Column(name = "LAST_")
    protected String lastName;

    @Column(name = "EMAIL_")
    protected String email;

    @Column(name = "PWD_")
    protected String password;

    @Column(name = "PICTURE_ID_")
    protected String picture;

    @Column(name = "PROFILE_ID_")
    protected String profile;

    @Column(name = "IS_ENABLED_")
    protected boolean isEnabled;
    
    @Column(name = "IS_BLOCKED_FOR_ATTEMPS")
    protected boolean isBlockedForAttemps;

    @Column(name = "RECEIVE_EMAIL_")
    protected boolean isReceiveEmail;

    @Column(name = "RECEIVE_NOTIFICATION_")
    protected boolean isReceiveNotification;

    @OneToMany(cascade = {CascadeType.ALL}, orphanRemoval = true, fetch = FetchType.EAGER, mappedBy = "userId")
    //@JoinColumn(name = "USER_ID_", referencedColumnName = "ID_")
    protected Set<GroupMemberActiviti> groups;

    @OneToMany(cascade = {CascadeType.ALL}, orphanRemoval = true, fetch = FetchType.EAGER, mappedBy = "userId")
    //@JoinColumn(name = "USER_ID_",referencedColumnName = "ID_")
    protected List<UserInfoActiviti> info;

    @PrePersist
    public void setValues() {
        this.isEnabled = true;

        if (Optional.ofNullable(this.groups).isPresent() && !this.groups.isEmpty()) {
            this.groups.forEach(g -> {
                g.setUserId(this.id);
            });
        }

        if (Optional.ofNullable(this.info).isPresent() && !this.info.isEmpty()) {
            this.info.forEach(i -> {
                i.setUserId(this.id);
            });
        }

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
