package com.worfwint.tabletoprpgmanager.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.worfwint.tabletoprpgmanager.dto.response.PageResponse;
import com.worfwint.tabletoprpgmanager.dto.response.UserFullProfileResponse;
import com.worfwint.tabletoprpgmanager.dto.response.UserPublicProfileResponse;
import com.worfwint.tabletoprpgmanager.dto.response.UserSearchInfoResponse;
import com.worfwint.tabletoprpgmanager.entity.User;
import com.worfwint.tabletoprpgmanager.exception.UserNotFoundException;
import com.worfwint.tabletoprpgmanager.repository.UserRepository;

/**
 * Provides user-related read operations and mapping logic for API responses.
 */
@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    // private final PasswordEncoder passwordEncoder;

    /**
     * Creates a new {@link UserService} with the required dependencies.
     *
     * @param userRepository repository used to fetch user data
     */
    public UserService(UserRepository userRepository/*, PasswordEncoder passwordEncoder*/) {
        this.userRepository = userRepository;
        // this.passwordEncoder = passwordEncoder;
    }

    /**
     * Returns the public profile of the user with the provided identifier.
     *
     * @param userId identifier of the user to lookup
     * @return DTO representing the public profile
     */
    public UserPublicProfileResponse getPublicUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
        return mapToUserPublicProfileResponse(user);
    }

    /**
     * Returns the full profile of the user with the provided identifier.
     *
     * @param userId identifier of the user to lookup
     * @return DTO representing the full profile
     */
    public UserFullProfileResponse getFullUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
        return mapToUserFullProfileResponse(user);
    }

    /**
     * Returns the public profile of the user with the provided username.
     *
     * @param username username to search for
     * @return DTO representing the public profile
     */
    public UserPublicProfileResponse getPublicUserProfileByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(UserNotFoundException::new);
        return mapToUserPublicProfileResponse(user);
    }

    /**
     * Returns the full profile of the user with the provided username.
     *
     * @param username username to search for
     * @return DTO representing the full profile
     */
    public UserFullProfileResponse getFullUserProfileByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(UserNotFoundException::new);
        return mapToUserFullProfileResponse(user);
    }

    // TODO(michael): update profile

    // TODO(michael): change password

    /**
     * Retrieves a paginated list of public user profiles.
     *
     * @param pageable pagination parameters
     * @return page of public profile DTOs
     */
    public PageResponse<UserPublicProfileResponse> getUsers(Pageable pageable) {
        Page<User> users = userRepository.findAll(pageable);
        return PageResponse.from(users.map(this::mapToUserPublicProfileResponse));
    }

    /**
     * Searches for users by username fragment and returns lightweight search results.
     *
     * @param username optional username fragment to filter by
     * @param pageable pagination parameters
     * @return page of search result DTOs
     */
    public PageResponse<UserSearchInfoResponse> searchUsers(String username, Pageable pageable) {
        Page<User> usersPage;
        if (username == null || username.isBlank()) {
            usersPage = userRepository.findAll(pageable);
        } else {
            usersPage = userRepository.findByUsernameContainingIgnoreCase(username.trim(), pageable);
        }
        return PageResponse.from(usersPage.map(this::mapToUserSearchInfoResponse));
    }

    /**
     * Deletes the user with the provided identifier.
     *
     * @param userId identifier of the user to delete
     */
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException();
        }
        userRepository.deleteById(userId);
    }

    /**
     * Checks whether the provided identifier belongs to the supplied username.
     *
     * @param userId identifier to check
     * @param username username to compare against
     * @return {@code true} when the identifier belongs to the username
     */
    public boolean isCurrentUser(Long userId, String username) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(UserNotFoundException::new);
            return user.getUsername().equals(username);
        } catch (UserNotFoundException e) {
            return false;
        }
    }

    /**
     * Maps a {@link User} entity to a {@link UserFullProfileResponse}.
     *
     * @param user entity to map
     * @return fully populated profile DTO
     */
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

    /**
     * Maps a {@link User} entity to a {@link UserPublicProfileResponse}.
     *
     * @param user entity to map
     * @return public profile DTO
     */
    private UserPublicProfileResponse mapToUserPublicProfileResponse(User user) {
        return new UserPublicProfileResponse(
                user.getId(),
                user.getUsername(),
                user.getDisplayName(),
                user.getBio(),
                user.getAvatarUrl()
        );
    }

    /**
     * Maps a {@link User} entity to a {@link UserSearchInfoResponse}.
     *
     * @param user entity to map
     * @return search result DTO
     */
    private UserSearchInfoResponse mapToUserSearchInfoResponse(User user) {
        return new UserSearchInfoResponse(
                user.getId(),
                user.getUsername(),
                user.getDisplayName(),
                user.getAvatarUrl()
        );
    }
}
