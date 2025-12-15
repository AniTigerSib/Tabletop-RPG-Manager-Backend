package com.worfwint.tabletoprpgmanager.news.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Response model used to share the current like status for an article.
 */
@Schema(description = "Current like state for the requesting user and aggregate counts.")
public class NewsLikeStatusResponse {

    @Schema(description = "Total number of likes the article has received")
    private final long likeCount;
    @Schema(description = "Indicates whether the requesting user has liked the article")
    private final boolean liked;

    public NewsLikeStatusResponse(long likeCount, boolean liked) {
        this.likeCount = likeCount;
        this.liked = liked;
    }

    public long getLikeCount() {
        return likeCount;
    }

    public boolean isLiked() {
        return liked;
    }
}
