package com.worfwint.tabletoprpgmanager.dto.response;

import java.time.LocalDateTime;
import java.util.Set;

import com.worfwint.tabletoprpgmanager.entity.enums.UserRole;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Response containing the complete user profile details visible to privileged users.
 */
@Schema(description = "Complete user profile returned to the account owner and privileged roles.")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserFullProfileResponse {

    /**
     * Unique identifier of the user.
     */
    @Schema(description = "Unique identifier of the user")
    private Long id;

    /**
     * Username chosen by the user.
     */
    @Schema(description = "Username chosen by the user")
    private String username;

    /**
     * Email address associated with the user account.
     */
    @Schema(description = "Email address associated with the user account")
    private String email;

    /**
     * Display name shown to other users.
     */
    @Schema(description = "Display name shown to other users")
    private String displayName;

    /**
     * Biography or profile description.
     */
    @Schema(description = "Biography or profile description", nullable = true)
    private String bio;

    /**
     * URL pointing to the user's avatar image.
     */
    @Schema(description = "URL pointing to the user's avatar image", nullable = true)
    private String avatarUrl;

    /**
     * Set of roles assigned to the user.
     */
    @Schema(description = "Set of roles assigned to the user")
    private Set<UserRole> roles;

    /**
     * Timestamp when the user account was created.
     */
    @Schema(description = "Timestamp when the user account was created")
    private LocalDateTime createdAt;

    /**
     * Timestamp when the user account was last updated.
     */
    @Schema(description = "Timestamp when the user account was last updated")
    private LocalDateTime updatedAt;
}
