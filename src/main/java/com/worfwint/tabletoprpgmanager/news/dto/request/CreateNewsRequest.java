package com.worfwint.tabletoprpgmanager.news.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

/**
 * Request payload for creating a new news article.
 */
@Getter
public class CreateNewsRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title must be 200 characters or fewer")
    private String title;

    @Size(max = 512, message = "Summary must be 512 characters or fewer")
    private String summary;

    @NotBlank(message = "Content is required")
    private String content;

    public void setTitle(String title) {
        this.title = title;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
