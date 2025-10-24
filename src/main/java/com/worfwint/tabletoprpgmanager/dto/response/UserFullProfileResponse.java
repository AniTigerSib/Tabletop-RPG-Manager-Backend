package com.worfwint.tabletoprpgmanager.dto.response;

import java.time.LocalDateTime;
import java.util.Set;

import com.worfwint.tabletoprpgmanager.entity.enums.UserRole;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response containing the complete user profile details visible to privileged users.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserFullProfileResponse {

    /**
     * Unique identifier of the user.
     */
    private Long id;

    /**
     * Username chosen by the user.
     */
    private String username;

    /**
     * Email address associated with the user account.
     */
    private String email;

    /**
     * Display name shown to other users.
     */
    private String displayName;

    /**
     * Biography or profile description.
     */
    private String bio;

    /**
     * URL pointing to the user's avatar image.
     */
    private String avatarUrl;

    /**
     * Set of roles assigned to the user.
     */
    private Set<UserRole> roles;

    /**
     * Timestamp when the user account was created.
     */
    private LocalDateTime createdAt;

    /**
     * Timestamp when the user account was last updated.
     */
    private LocalDateTime updatedAt;
}
