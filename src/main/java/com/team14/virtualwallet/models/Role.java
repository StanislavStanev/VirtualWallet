package com.team14.virtualwallet.models;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "roles")
@Getter
public class Role extends BaseEntity implements GrantedAuthority {

    @Column(name = "authority")
    @ApiModelProperty(notes = "Spring security authority USER/ADMIN")
    private String authority;

    public Role() {
    }

    @Override
    public String getAuthority() {
        return this.authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }
}
