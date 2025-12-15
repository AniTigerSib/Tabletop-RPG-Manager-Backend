package com.worfwint.tabletoprpgmanager.news.dto.response;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Response model representing a single news comment.
 */
@Schema(description = "Representation of a news article comment including authorship metadata.")
public class NewsCommentResponse {

    @Schema(description = "Unique identifier of the comment")
    private final Long id;
    @Schema(description = "Body text of the comment")
    private final String content;
    @Schema(description = "Timestamp when the comment was created")
    private final LocalDateTime createdAt;
    @Schema(description = "Timestamp of the latest update")
    private final LocalDateTime updatedAt;
    @Schema(description = "Indicates if the authenticated user authored the comment")
    private final boolean ownedByCurrentUser;
    @Schema(description = "Author of the comment")
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
