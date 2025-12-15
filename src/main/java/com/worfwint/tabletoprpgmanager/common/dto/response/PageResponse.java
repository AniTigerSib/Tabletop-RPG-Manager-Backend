package com.worfwint.tabletoprpgmanager.common.dto.response;

import java.util.List;

import org.springframework.data.domain.Page;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Generic pagination wrapper for API responses.
 *
 * @param <T>           type of the elements contained in the page
 * @param content       Elements contained in the current page.
 * @param page          Zero-based index of the current page.
 * @param size          Page size requested by the client.
 * @param totalElements Total number of elements across all pages.
 * @param totalPages    Total number of pages available for the query.
 * @param last          Indicates whether the current page is the last one.
 */
@Schema(description = "Generic pagination envelope returned by list endpoints.")
public record PageResponse<T>(@Schema(description = "Elements contained in the current page") List<T> content,
                              @Schema(description = "Zero-based index of the current page") int page,
                              @Schema(description = "Number of elements requested for each page") int size,
                              @Schema(description = "Total number of elements available for the query") long totalElements,
                              @Schema(description = "Total number of pages that can be retrieved") int totalPages,
                              @Schema(description = "Indicates whether this page is the last page") boolean last) {

    /**
     * Creates a new paginated response.
     *
     * @param content       elements contained in the page
     * @param page          zero-based page index
     * @param size          size of the page
     * @param totalElements total number of elements
     * @param totalPages    total number of pages
     * @param last          whether this is the last page
     */
    public PageResponse(List<T> content,
                        int page,
                        int size,
                        long totalElements,
                        int totalPages,
                        boolean last) {
        this.content = content;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.last = last;
    }

    /**
     * Creates a {@link PageResponse} from a Spring Data {@link Page}.
     *
     * @param page result page from a repository call
     * @param <T>  type of the elements contained in the page
     * @return a new {@link PageResponse} with the same data
     */
    public static <T> PageResponse<T> from(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }

}
