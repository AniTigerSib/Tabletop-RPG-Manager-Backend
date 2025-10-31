package com.worfwint.tabletoprpgmanager.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Response item representing a user in search results.
 */
@Schema(description = "Search result entry describing a matching user.")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSearchInfoResponse {

    /**
     * Unique identifier of the user.
     */
    @Schema(description = "Unique identifier of the user")
    private Long id;

    /**
     * Username matching the search query.
     */
    @Schema(description = "Username matching the search query")
    private String username;

    /**
     * Display name shown alongside the username.
     */
    @Schema(description = "Display name shown alongside the username")
    private String displayName;

    /**
     * URL to the avatar shown in search results.
     */
    @Schema(description = "URL to the avatar shown in search results", nullable = true)
    private String avatarUrl;
}
