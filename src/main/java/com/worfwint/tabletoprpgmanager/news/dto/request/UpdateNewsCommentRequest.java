package com.worfwint.tabletoprpgmanager.news.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

/**
 * Request payload for updating a news comment.
 */
@Getter
public class UpdateNewsCommentRequest {

    @NotBlank(message = "Content is required")
    @Size(max = 2000, message = "Content must be 2000 characters or fewer")
    private String content;

    public void setContent(String content) {
        this.content = content;
    }
}
