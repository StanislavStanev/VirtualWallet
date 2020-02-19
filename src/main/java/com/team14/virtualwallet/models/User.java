package com.team14.virtualwallet.models;

import io.swagger.annotations.ApiModelProperty;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
public class User extends BaseEntity implements UserDetails {

    @Column(name = "username", unique = true, nullable = false)
    @Size(min = 5, max = 50, message = "User name size should be between 5 and 50 symbols")
    @ApiModelProperty(notes = "User unique username.")
    private String username;

    @Column(name = "email", unique = true, nullable = false)
    @Email
    @ApiModelProperty(notes = "User email. On this mail address registration will be send.")
    private String email;

    @Column(name = "password", nullable = false)
    @ApiModelProperty(notes = "User password")
    private String password;

    @Column(name = "created_on", nullable = false)
    @ApiModelProperty(notes = "User password.")
    private LocalDateTime createdOn;

    @Column(name = "roles")
    @ManyToMany(targetEntity = Role.class, fetch = FetchType.EAGER)
    @JoinTable(name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id")
    )
    @ApiModelProperty(notes = "User roles.")
    private Set<Role> authorities;

    @Column(name = "is_account_non_expired")
    @ApiModelProperty(notes = "Account is expired.")
    private boolean isAccountNonExpired;

    @Column(name = "is_account_non_locked")
    @ApiModelProperty(notes = "Account not locked.")
    private boolean isAccountNonLocked;

    @Column(name = "is_credentials_non_expired")
    @ApiModelProperty(notes = "Password not expired.")
    private boolean isCredentialsNonExpired;

    @Column(name = "is_enabled")
    @ApiModelProperty(notes = "User is enabled")
    private boolean isEnabled;

    @Column(name = "is_blocked", nullable = false)
    @ApiModelProperty(notes = "User is blocked")
    private Boolean isBlocked = false;

    public User() {
        this.authorities = new HashSet<>();
    }

    @Override
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(LocalDateTime createdOn) {
        this.createdOn = createdOn;
    }

    @Override
    public Set<Role> getAuthorities() {
        return this.authorities;
    }

    public void setAuthorities(Set<Role> authorities) {
        this.authorities = authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.isAccountNonExpired;
    }

    public void setAccountNonExpired(boolean accountNonExpired) {
        isAccountNonExpired = accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.isAccountNonLocked;
    }

    public void setAccountNonLocked(boolean accountNonLocked) {
        isAccountNonLocked = accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.isCredentialsNonExpired;
    }

    public void setCredentialsNonExpired(boolean credentialsNonExpired) {
        isCredentialsNonExpired = credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return this.isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
    }
}