package com.worfwint.tabletoprpgmanager.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response item representing a user in search results.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSearchInfoResponse {

    /**
     * Unique identifier of the user.
     */
    private Long id;

    /**
     * Username matching the search query.
     */
    private String username;

    /**
     * Display name shown alongside the username.
     */
    private String displayName;

    /**
     * URL to the avatar shown in search results.
     */
    private String avatarUrl;
}
