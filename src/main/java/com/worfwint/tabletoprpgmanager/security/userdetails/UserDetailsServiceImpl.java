package com.worfwint.tabletoprpgmanager.security.userdetails;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.worfwint.tabletoprpgmanager.entity.User;
import com.worfwint.tabletoprpgmanager.repository.UserRepository;

/**
 * Loads user details for Spring Security based on username or identifier.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Creates the service with the required dependencies.
     *
     * @param userRepository repository used to look up users
     */
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Loads an authenticated user by username.
     *
     * @param username username to look up
     * @return {@link UserDetails} for the matched user
     * @throws UsernameNotFoundException when no user matches the supplied username
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                     .orElseThrow(() -> new UsernameNotFoundException("Not found"));
        List<GrantedAuthority> auth = user.getRoles().stream()
                 .map(role -> new SimpleGrantedAuthority(role.name()))
                 .collect(Collectors.toList());
        return new UserDetailsImpl(user.getId(), user.getUsername(), user.getEmail(), user.getPasswordHash(), auth);
    }

    /**
     * Loads an authenticated user by identifier.
     *
     * @param userId identifier to look up
     * @return {@link UserDetails} for the matched user
     * @throws UsernameNotFoundException when no user matches the supplied identifier
     */
    public UserDetails loadUserById(Long userId) throws UsernameNotFoundException {
        User user = userRepository.findById(userId)
                     .orElseThrow(() -> new UsernameNotFoundException("Not found"));
        List<GrantedAuthority> auth = user.getRoles().stream()
                 .map(role -> new SimpleGrantedAuthority(role.name()))
                 .collect(Collectors.toList());
        return new UserDetailsImpl(user.getId(), user.getUsername(), user.getEmail(), user.getPasswordHash(), auth);
    }
}
