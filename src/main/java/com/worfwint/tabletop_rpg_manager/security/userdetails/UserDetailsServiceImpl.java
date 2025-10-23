package com.worfwint.tabletop_rpg_manager.security.userdetails;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.worfwint.tabletop_rpg_manager.entity.User;
import com.worfwint.tabletop_rpg_manager.repository.UserRepository;

/**
 *
 * @author michael
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService{

    private final UserRepository userRepository;
    
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                     .orElseThrow(() -> new UsernameNotFoundException("Not found"));
        List<GrantedAuthority> auth = user.getRoles().stream()
                 .map(r -> new SimpleGrantedAuthority("ROLE_" + r)) // TODO: check how it works it later
                 .collect(Collectors.toList());
        return new UserDetailsImpl(user.getId(), user.getUsername(), user.getEmail(), user.getPasswordHash(), auth);
    }

    public UserDetails loadUserById(Long userId) throws UsernameNotFoundException {
        User user = userRepository.findById(userId)
                     .orElseThrow(() -> new UsernameNotFoundException("Not found"));
        List<GrantedAuthority> auth = user.getRoles().stream()
                 .map(r -> new SimpleGrantedAuthority("ROLE_" + r)) // TODO: check how it works it later
                 .collect(Collectors.toList());
        return new UserDetailsImpl(user.getId(), user.getUsername(), user.getEmail(), user.getPasswordHash(), auth);
    }
}
