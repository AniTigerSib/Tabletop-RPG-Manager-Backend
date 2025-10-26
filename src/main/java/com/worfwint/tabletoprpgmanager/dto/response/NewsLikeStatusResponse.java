package com.worfwint.tabletoprpgmanager.dto.response;

/**
 * Response model used to share the current like status for an article.
 */
public class NewsLikeStatusResponse {

    private final long likeCount;
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
