package com.worfwint.tabletoprpgmanager.user.dto.response;

import java.time.LocalDateTime;
import java.util.Set;

import com.worfwint.tabletoprpgmanager.user.entity.UserRole;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Response containing the complete user profile details visible to privileged users.
 *
 * @param id          Unique identifier of the user.
 * @param username    Username chosen by the user.
 * @param email       Email address associated with the user account.
 * @param displayName Display name shown to other users.
 * @param bio         Biography or profile description.
 * @param avatarUrl   URL pointing to the user's avatar image.
 * @param roles       Set of roles assigned to the user.
 * @param createdAt   Timestamp when the user account was created.
 * @param updatedAt   Timestamp when the user account was last updated.
 */
@Schema(description = "Complete user profile returned to the privileged roles.")
public record UserFullProfileResponse(@Schema(description = "Unique identifier of the user") Long id,
                                      @Schema(description = "Username chosen by the user") String username,
                                      @Schema(description = "Email address associated with the user account") String email,
                                      @Schema(description = "Display name shown to other users") String displayName,
                                      @Schema(description = "Biography or profile description", nullable = true) String bio,
                                      @Schema(description = "URL pointing to the user's avatar image", nullable = true) String avatarUrl,
                                      @Schema(description = "Set of roles assigned to the user") Set<UserRole> roles,
                                      @Schema(description = "Timestamp when the user account was created") LocalDateTime createdAt,
                                      @Schema(description = "Timestamp when the user account was last updated") LocalDateTime updatedAt) {

}
