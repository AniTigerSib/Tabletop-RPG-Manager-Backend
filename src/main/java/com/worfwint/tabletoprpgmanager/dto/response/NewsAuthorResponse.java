package com.worfwint.tabletoprpgmanager.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Lightweight representation of a news author used in API responses.
 */
@Schema(description = "Lightweight representation of a news author used in API responses.")
public class NewsAuthorResponse {

    @Schema(description = "Unique identifier of the author")
    private final Long id;
    @Schema(description = "Username of the author")
    private final String username;
    @Schema(description = "Display name presented to users")
    private final String displayName;
    @Schema(description = "URL pointing to the author's avatar image", nullable = true)
    private final String avatarUrl;

    public NewsAuthorResponse(Long id,
                              String username,
                              String displayName,
                              String avatarUrl) {
        this.id = id;
        this.username = username;
        this.displayName = displayName;
        this.avatarUrl = avatarUrl;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }
}
