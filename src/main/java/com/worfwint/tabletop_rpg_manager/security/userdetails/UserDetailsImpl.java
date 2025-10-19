package com.worfwint.tabletop_rpg_manager.security.userdetails;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 *
 * @author michael
 */
@Getter
@EqualsAndHashCode
public class UserDetailsImpl implements UserDetails {

    private final Long id;
    private final String username;
    private final String email;
    private final String password;
    private final List<GrantedAuthority> authoriries;

    public UserDetailsImpl(Long id, String username, String email, String passwordHash, List<GrantedAuthority> authoriries) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = passwordHash;
        this.authoriries = authoriries;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() { return authoriries; }

    @Override
    public String getPassword() { return password; }

    @Override
    public String getUsername() { return username; }
    
    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }
    
    @Override
    public boolean isEnabled() { return true; }
}
