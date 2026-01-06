package com.worfwint.tabletoprpgmanager.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Response containing the subset of user information that is publicly visible.
 *
 * @param id          Unique identifier of the user.
 * @param username    Username shown in the public profile.
 * @param displayName Display name presented to other users.
 * @param bio         Short biography shared on the profile page.
 * @param avatarUrl   URL pointing to the user's avatar image.
 */
@Schema(description = "Publicly visible portion of a user's profile.")
public record UserPublicProfileResponse(@Schema(description = "Unique identifier of the user") Long id,
                                        @Schema(description = "Username shown in the public profile") String username,
                                        @Schema(description = "Display name presented to other users") String displayName,
                                        @Schema(description = "Short biography shared on the profile page", nullable = true) String bio,
                                        @Schema(description = "URL pointing to the user's avatar image", nullable = true) String avatarUrl) {

}
