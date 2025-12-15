package com.worfwint.tabletoprpgmanager.auth.security.userdetails;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Custom {@link UserDetails} implementation wrapping the application's {@code User} entity.
 */
@Getter
@EqualsAndHashCode
public class UserDetailsImpl implements UserDetails {
    private static final long serialVersionUID = 1L;

    private final Long id;
    private final String username;
    private final String email;
    private final String password;
    private final List<GrantedAuthority> authorities;

    /**
     * Creates a new {@link UserDetails} instance with the provided user attributes.
     *
     * @param id database identifier of the user
     * @param username username used for login
     * @param email email address of the user
     * @param passwordHash hashed password used by Spring Security
     * @param authorities granted authorities for authorization checks
     */
    public UserDetailsImpl(Long id,
                           String username,
                           String email,
                           String passwordHash,
                           List<GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = passwordHash;
        this.authorities = List.copyOf(authorities);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.copyOf(authorities);
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
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
}
