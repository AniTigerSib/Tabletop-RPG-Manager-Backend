package com.worfwint.tabletoprpgmanager.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Response containing the subset of user information that is publicly visible.
 */
@Schema(description = "Publicly visible portion of a user's profile.")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPublicProfileResponse {

    /**
     * Unique identifier of the user.
     */
    @Schema(description = "Unique identifier of the user")
    private Long id;

    /**
     * Username shown in the public profile.
     */
    @Schema(description = "Username shown in the public profile")
    private String username;

    /**
     * Display name presented to other users.
     */
    @Schema(description = "Display name presented to other users")
    private String displayName;

    /**
     * Short biography shared on the profile page.
     */
    @Schema(description = "Short biography shared on the profile page", nullable = true)
    private String bio;

    /**
     * URL pointing to the user's avatar image.
     */
    @Schema(description = "URL pointing to the user's avatar image", nullable = true)
    private String avatarUrl;
}
