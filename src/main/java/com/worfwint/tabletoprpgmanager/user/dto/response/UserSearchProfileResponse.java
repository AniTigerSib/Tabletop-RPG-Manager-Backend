package com.worfwint.tabletoprpgmanager.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Response item representing a user in search results.
 *
 * @param id          Unique identifier of the user.
 * @param username    Username matching the search query.
 * @param displayName Display name shown alongside the username.
 * @param avatarUrl   URL to the avatar shown in search results.
 */
@Schema(description = "Search result entry describing a matching user.")
public record UserSearchProfileResponse(@Schema(description = "Unique identifier of the user") Long id,
                                        @Schema(description = "Username matching the search query") String username,
                                        @Schema(description = "Display name shown alongside the username") String displayName,
                                        @Schema(description = "URL to the avatar shown in search results", nullable = true) String avatarUrl) {

}
