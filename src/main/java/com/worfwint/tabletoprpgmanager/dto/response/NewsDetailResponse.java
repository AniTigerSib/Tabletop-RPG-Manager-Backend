package com.worfwint.tabletoprpgmanager.dto.response;

import java.time.LocalDateTime;

/**
 * Response model describing a full news article with author metadata.
 */
public class NewsDetailResponse {

    private final Long id;
    private final String title;
    private final String summary;
    private final String content;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final long likeCount;
    private final long commentCount;
    private final boolean likedByCurrentUser;
    private final NewsAuthorResponse author;

    public NewsDetailResponse(Long id,
                              String title,
                              String summary,
                              String content,
                              LocalDateTime createdAt,
                              LocalDateTime updatedAt,
                              long likeCount,
                              long commentCount,
                              boolean likedByCurrentUser,
                              NewsAuthorResponse author) {
        this.id = id;
        this.title = title;
        this.summary = summary;
        this.content = content;
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

    public String getContent() {
        return content;
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
