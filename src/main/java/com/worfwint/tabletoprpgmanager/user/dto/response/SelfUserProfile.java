package com.worfwint.tabletoprpgmanager.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * Response containing the user profile details visible to user himself.
 *
 * @param id          Unique identifier of the user.
 * @param username    Username chosen by the user.
 * @param email       Email address associated with the user account.
 * @param displayName Display name shown to other users.
 * @param bio         Biography or profile description.
 * @param avatarUrl   URL pointing to the user's avatar image.
 */
@Schema(description = "User self profile returned to the account owner.")
public record SelfUserProfile(@Schema(description = "Unique identifier of the user") Long id,
                              @Schema(description = "Username chosen by the user") String username,
                              @Schema(description = "Email address associated with the user account") String email,
                              @Schema(description = "Display name shown to other users") String displayName,
                              @Schema(description = "Biography or profile description", nullable = true) String bio,
                              @Schema(description = "URL pointing to the user's avatar image", nullable = true) String avatarUrl,
                              @Schema(description = "Timestamp when the user account was created") LocalDateTime createdAt) {
}
