package com.worfwint.tabletoprpgmanager.news.dto.response;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Response model describing a news article summary.
 */
@Schema(description = "Summary representation of a news article displayed in paginated lists.")
public class NewsSummaryResponse {

    @Schema(description = "Unique identifier of the article")
    private final Long id;
    @Schema(description = "Title presented to readers")
    private final String title;
    @Schema(description = "Short teaser displayed alongside the title")
    private final String summary;
    @Schema(description = "Timestamp when the article was created")
    private final LocalDateTime createdAt;
    @Schema(description = "Timestamp of the latest update")
    private final LocalDateTime updatedAt;
    @Schema(description = "Number of likes the article has received")
    private final long likeCount;
    @Schema(description = "Number of comments associated with the article")
    private final long commentCount;
    @Schema(description = "Indicates if the current viewer has liked the article")
    private final boolean likedByCurrentUser;
    @Schema(description = "Author of the article")
    private final NewsAuthorResponse author;

    public NewsSummaryResponse(Long id,
                               String title,
                               String summary,
                               LocalDateTime createdAt,
                               LocalDateTime updatedAt,
                               long likeCount,
                               long commentCount,
                               boolean likedByCurrentUser,
                               NewsAuthorResponse author) {
        this.id = id;
        this.title = title;
        this.summary = summary;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.likedByCurrentUser = likedByCurrentUser;
        this.author = author;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getSummary() {
        return summary;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public long getLikeCount() {
        return likeCount;
    }

    public long getCommentCount() {
        return commentCount;
    }

    public boolean isLikedByCurrentUser() {
        return likedByCurrentUser;
    }

    public NewsAuthorResponse getAuthor() {
        return author;
    }
}
