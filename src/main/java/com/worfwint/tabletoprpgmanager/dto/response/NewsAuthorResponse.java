package com.worfwint.tabletoprpgmanager.dto.response;

/**
 * Lightweight representation of a news author used in API responses.
 */
public class NewsAuthorResponse {

    private final Long id;
    private final String username;
    private final String displayName;
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
