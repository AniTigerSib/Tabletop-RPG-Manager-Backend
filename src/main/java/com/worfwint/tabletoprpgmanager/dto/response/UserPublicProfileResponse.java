package com.worfwint.tabletoprpgmanager.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response containing the subset of user information that is publicly visible.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPublicProfileResponse {

    /**
     * Unique identifier of the user.
     */
    private Long id;

    /**
     * Username shown in the public profile.
     */
    private String username;

    /**
     * Display name presented to other users.
     */
    private String displayName;

    /**
     * Short biography shared on the profile page.
     */
    private String bio;

    /**
     * URL pointing to the user's avatar image.
     */
    private String avatarUrl;
}
