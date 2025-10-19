package com.worfwint.tabletop_rpg_manager.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.worfwint.tabletop_rpg_manager.dto.response.UserFullProfileResponse;
import com.worfwint.tabletop_rpg_manager.dto.response.UserPublicProfileResponse;
import com.worfwint.tabletop_rpg_manager.dto.response.UserSearchInfoResponse;
import com.worfwint.tabletop_rpg_manager.entity.User;
import com.worfwint.tabletop_rpg_manager.repository.UserRepository;

/**
 *
 * @author michael
 */
@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    // private final PasswordEncoder passwordEncoder;

    // @Autowired
    public UserService(UserRepository userRepository/*, PasswordEncoder passwordEncoder*/) {
        this.userRepository = userRepository;
        // this.passwordEncoder = passwordEncoder;
    }

    public UserPublicProfileResponse getPublicUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return mapToUserPublicProfileResponse(user);
    }

    public UserFullProfileResponse getFullUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return mapToUserFullProfileResponse(user);
    }

    public UserPublicProfileResponse getPublicUserProfileByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return mapToUserPublicProfileResponse(user);
    }

    public UserFullProfileResponse getFullUserProfileByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return mapToUserFullProfileResponse(user);
    }

    // TODO(michael): update profile

    // TODO(michael): change password

    public List<UserPublicProfileResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToUserPublicProfileResponse)
                .collect(Collectors.toList());
    }

    public List<UserSearchInfoResponse> searchUsersByUsername(String username) {
        return userRepository.findByUsernameContainingIgnoreCase(username).stream()
                .map(this::mapToUserSearchInfoRespose)
                .collect(Collectors.toList());
    }

    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found");
        }
        userRepository.deleteById(userId);
    }

    public boolean isCurrentUser(Long userId, String username) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            return user.getUsername().equals(username);
        } catch (Exception e) {
            return false;
        }
    }

    private UserFullProfileResponse mapToUserFullProfileResponse(User user) {
        return new UserFullProfileResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getDisplayName(),
                user.getBio(),
                user.getAvatarUrl(),
                user.getRoles(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

    private UserPublicProfileResponse mapToUserPublicProfileResponse(User user) {
        return new UserPublicProfileResponse(
                user.getId(),
                user.getUsername(),
                user.getDisplayName(),
                user.getBio(),
                user.getAvatarUrl()
        );
    }

    private UserSearchInfoResponse mapToUserSearchInfoRespose(User user) {
        return new UserSearchInfoResponse(
                user.getId(),
                user.getUsername(),
                user.getDisplayName(),
                user.getAvatarUrl()
        );
    }
}
