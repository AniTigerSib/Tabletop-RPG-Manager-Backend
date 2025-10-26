package com.worfwint.tabletoprpgmanager.dto.response;

import java.time.LocalDateTime;

/**
 * Response model representing a single news comment.
 */
public class NewsCommentResponse {

    private final Long id;
    private final String content;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final boolean ownedByCurrentUser;
    private final NewsAuthorResponse author;

    public NewsCommentResponse(Long id,
                               String content,
                               LocalDateTime createdAt,
                               LocalDateTime updatedAt,
                               boolean ownedByCurrentUser,
                               NewsAuthorResponse author) {
        this.id = id;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.ownedByCurrentUser = ownedByCurrentUser;
        this.author = author;
    }

    public Long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public boolean isOwnedByCurrentUser() {
        return ownedByCurrentUser;
    }

    public NewsAuthorResponse getAuthor() {
        return author;
    }
}
