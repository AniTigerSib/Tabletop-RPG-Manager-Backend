package com.worfwint.tabletoprpgmanager.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request payload for creating a news comment.
 */
public class CreateNewsCommentRequest {

    @NotBlank(message = "Content is required")
    @Size(max = 2000, message = "Content must be 2000 characters or fewer")
    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
