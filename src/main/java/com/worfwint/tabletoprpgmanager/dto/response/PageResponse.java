package com.worfwint.tabletoprpgmanager.dto.response;

import java.util.List;

import org.springframework.data.domain.Page;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Generic pagination wrapper for API responses.
 *
 * @param <T> type of the elements contained in the page
 */
@Schema(description = "Generic pagination envelope returned by list endpoints.")
public class PageResponse<T> {

    /**
     * Elements contained in the current page.
     */
    @Schema(description = "Elements contained in the current page")
    private final List<T> content;

    /**
     * Zero-based index of the current page.
     */
    @Schema(description = "Zero-based index of the current page")
    private final int page;

    /**
     * Page size requested by the client.
     */
    @Schema(description = "Number of elements requested for each page")
    private final int size;

    /**
     * Total number of elements across all pages.
     */
    @Schema(description = "Total number of elements available for the query")
    private final long totalElements;

    /**
     * Total number of pages available for the query.
     */
    @Schema(description = "Total number of pages that can be retrieved")
    private final int totalPages;

    /**
     * Indicates whether the current page is the last one.
     */
    @Schema(description = "Indicates whether this page is the last page")
    private final boolean last;

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
     * @param <T> type of the elements contained in the page
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

    /**
     * @return elements contained in the current page
     */
    public List<T> getContent() {
        return content;
    }

    /**
     * @return zero-based index of the current page
     */
    public int getPage() {
        return page;
    }

    /**
     * @return page size requested by the client
     */
    public int getSize() {
        return size;
    }

    /**
     * @return total number of elements across all pages
     */
    public long getTotalElements() {
        return totalElements;
    }

    /**
     * @return total number of pages available for the query
     */
    public int getTotalPages() {
        return totalPages;
    }

    /**
     * @return {@code true} if this page is the last available page
     */
    public boolean isLast() {
        return last;
    }
}
