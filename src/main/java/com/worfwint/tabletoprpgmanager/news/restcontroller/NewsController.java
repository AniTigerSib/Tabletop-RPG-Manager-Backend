package com.worfwint.tabletoprpgmanager.news.restcontroller;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.worfwint.tabletoprpgmanager.common.dto.AuthenticatedUser;
import com.worfwint.tabletoprpgmanager.news.dto.request.CreateNewsCommentRequest;
import com.worfwint.tabletoprpgmanager.news.dto.request.CreateNewsRequest;
import com.worfwint.tabletoprpgmanager.news.dto.request.UpdateNewsCommentRequest;
import com.worfwint.tabletoprpgmanager.news.dto.request.UpdateNewsRequest;
import com.worfwint.tabletoprpgmanager.news.dto.response.NewsCommentResponse;
import com.worfwint.tabletoprpgmanager.news.dto.response.NewsDetailResponse;
import com.worfwint.tabletoprpgmanager.news.dto.response.NewsLikeStatusResponse;
import com.worfwint.tabletoprpgmanager.news.dto.response.NewsSummaryResponse;
import com.worfwint.tabletoprpgmanager.common.dto.response.PageResponse;
import com.worfwint.tabletoprpgmanager.common.exception.BadRequestException;
import com.worfwint.tabletoprpgmanager.news.exception.NewsArticleNotFoundException;
import com.worfwint.tabletoprpgmanager.news.exception.NewsCommentNotFoundException;
import com.worfwint.tabletoprpgmanager.common.exception.UnauthorizedException;
import com.worfwint.tabletoprpgmanager.news.service.NewsService;

import jakarta.validation.Valid;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

/**
 * REST controller handling operations related to application news.
 */
@RestController
@RequestMapping("/api/news")
@Validated
public class NewsController {

    private static final int DEFAULT_NEWS_PAGE_SIZE = 10;
    private static final int DEFAULT_COMMENT_PAGE_SIZE = 20;
    private static final int MAX_PAGE_SIZE = 50;

    private final NewsService newsService;

    public NewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    /**
     * Retrieves public news articles.
     *
     * @param page page index
     * @param size requested page size
     * @param authenticatedUser optional authenticated user
     * @return paginated response of news summaries
     */
    @Operation(
            summary = "List published news articles",
            description = "Returns the most recent news articles in reverse chronological order. The authenticated "
                    + "user context is used to calculate the like status."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Page of news articles returned successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PageResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Pagination parameters are invalid",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(implementation = String.class))
            )
    })
    @GetMapping({"", "/"})
    public PageResponse<NewsSummaryResponse> getNews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "" + DEFAULT_NEWS_PAGE_SIZE) int size,
            @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        Pageable pageable = buildPageRequest(page, size, Sort.by("createdAt").descending());
        return newsService.listArticles(pageable, authenticatedUser);
    }

    /**
     * Returns the detailed news article for the provided identifier.
     *
     * @param articleId identifier of the article
     * @param authenticatedUser optional authenticated user
     * @return detailed response
     */
    @Operation(
            summary = "Get news article details",
            description = "Provides the full contents of the article along with author details, like counts and "
                    + "whether the viewer has liked it."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "News article returned successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = NewsDetailResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "The requested article was not found",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(implementation = String.class))
            )
    })
    @GetMapping("/{articleId}")
    public NewsDetailResponse getNewsArticle(@PathVariable Long articleId,
                                             @Parameter(hidden = true)
                                             @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        return newsService.getArticle(articleId, authenticatedUser);
    }

    /**
     * Creates a new news article.
     *
     * @param request request payload
     * @param authenticatedUser authenticated user creating the article
     * @return created article response
     */
    @Operation(
            summary = "Create a news article",
            description = "Creates a new article using the submitted title, summary and body content."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Article created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = NewsDetailResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation failed for the article payload",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(implementation = String.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "The caller is not authenticated",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(implementation = String.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "The caller lacks permissions to publish news",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(implementation = String.class))
            )
    })
    @PreAuthorize("hasAnyRole('MODERATOR','ADMIN','DEVELOPER')")
    @PostMapping({"", "/"})
    public NewsDetailResponse createNews(@Valid @RequestBody CreateNewsRequest request,
                                         @Parameter(hidden = true)
                                         @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        return newsService.createArticle(authenticatedUser, request);
    }

    /**
     * Updates an existing news article.
     *
     * @param articleId identifier of the article
     * @param request request payload
     * @param authenticatedUser authenticated user performing the update
     * @return updated article response
     */
    @Operation(
            summary = "Update a news article",
            description = "Modifies the title, summary or content of an existing article."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Article updated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = NewsDetailResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation failed for the update payload",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(implementation = String.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "The caller is not authenticated",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(implementation = String.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "The caller lacks permissions to modify the article",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(implementation = String.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "The targeted article was not found",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(implementation = String.class))
            )
    })
    @PreAuthorize("hasAnyRole('MODERATOR','ADMIN','DEVELOPER')")
    @PutMapping("/{articleId}")
    public NewsDetailResponse updateNews(@PathVariable Long articleId,
                                         @Valid @RequestBody UpdateNewsRequest request,
                                         @Parameter(hidden = true)
                                         @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        return newsService.updateArticle(articleId, authenticatedUser, request);
    }

    /**
     * Deletes a news article.
     *
     * @param articleId identifier of the article
     * @param authenticatedUser authenticated user performing the deletion
     */
    @Operation(
            summary = "Delete a news article",
            description = "Removes the specified article permanently."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Article deleted successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Void.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "The caller is not authenticated",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(implementation = String.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "The caller lacks permissions to delete the article",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(implementation = String.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "The targeted article was not found",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(implementation = String.class))
            )
    })
    @PreAuthorize("hasAnyRole('MODERATOR','ADMIN','DEVELOPER')")
    @DeleteMapping("/{articleId}")
    public ResponseEntity<Void> deleteNews(@PathVariable Long articleId,
                                           @Parameter(hidden = true)
                                           @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        newsService.deleteArticle(articleId, authenticatedUser);
        return ResponseEntity.noContent().build();
    }

    /**
     * Returns paginated comments for a news article.
     *
     * @param articleId identifier of the article
     * @param page requested page index
     * @param size requested page size
     * @param authenticatedUser optional authenticated user
     * @return paginated comment response
     */
    @Operation(
            summary = "List article comments",
            description = "Returns a page of comments for the specified article sorted from newest to oldest."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Page of comments returned successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PageResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Pagination parameters are invalid",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(implementation = String.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "The article was not found",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(implementation = String.class))
            )
    })
    @GetMapping("/{articleId}/comments")
    public PageResponse<NewsCommentResponse> getNewsComments(
            @PathVariable Long articleId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "" + DEFAULT_COMMENT_PAGE_SIZE) int size,
            @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        Pageable pageable = buildPageRequest(page, size, Sort.by("createdAt").descending());
        return newsService.listComments(articleId, pageable, authenticatedUser);
    }

    /**
     * Adds a comment to a news article.
     *
     * @param articleId identifier of the article
     * @param request request payload
     * @param authenticatedUser authenticated user leaving the comment
     * @return created comment response
     */
    @Operation(
            summary = "Create a comment",
            description = "Adds a new comment to the specified article on behalf of the authenticated user."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Comment created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = NewsCommentResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation failed for the comment payload",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(implementation = String.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "The caller is not authenticated",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(implementation = String.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "The article was not found",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(implementation = String.class))
            )
    })
    @PostMapping("/{articleId}/comments")
    public NewsCommentResponse createNewsComment(@PathVariable Long articleId,
                                                 @Valid @RequestBody CreateNewsCommentRequest request,
                                                 @Parameter(hidden = true)
                                                 @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        return newsService.createComment(articleId, authenticatedUser, request);
    }

    /**
     * Updates an existing comment.
     *
     * @param articleId identifier of the article
     * @param commentId identifier of the comment
     * @param request request payload
     * @param authenticatedUser authenticated user performing the update
     * @return updated comment response
     */
    @Operation(
            summary = "Update a comment",
            description = "Edits the text of an existing comment when performed by its author or a moderator."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Comment updated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = NewsCommentResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation failed for the update payload",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(implementation = String.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "The caller is not authenticated",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(implementation = String.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "The caller is not permitted to edit the comment",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(implementation = String.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "The article or comment was not found",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(implementation = String.class))
            )
    })
    @PutMapping("/{articleId}/comments/{commentId}")
    public NewsCommentResponse updateNewsComment(@PathVariable Long articleId,
                                                 @PathVariable Long commentId,
                                                 @Valid @RequestBody UpdateNewsCommentRequest request,
                                                 @Parameter(hidden = true)
                                                 @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        return newsService.updateComment(articleId, commentId, authenticatedUser, request);
    }

    /**
     * Deletes a comment from a news article.
     *
     * @param articleId identifier of the article
     * @param commentId identifier of the comment
     * @param authenticatedUser authenticated user performing the deletion
     * @return no-content response
     */
    @Operation(
            summary = "Delete a comment",
            description = "Removes a comment from an article when invoked by its author or a privileged user."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Comment deleted successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Void.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "The caller is not authenticated",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(implementation = String.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "The caller is not permitted to delete the comment",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(implementation = String.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "The article or comment was not found",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(implementation = String.class))
            )
    })
    @DeleteMapping("/{articleId}/comments/{commentId}")
    public ResponseEntity<Void> deleteNewsComment(@PathVariable Long articleId,
                                                  @PathVariable Long commentId,
                                                  @Parameter(hidden = true)
                                                  @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        newsService.deleteComment(articleId, commentId, authenticatedUser);
        return ResponseEntity.noContent().build();
    }

    /**
     * Adds a like to a news article.
     *
     * @param articleId identifier of the article
     * @param authenticatedUser authenticated user liking the article
     * @return like status response
     */
    @Operation(
            summary = "Like an article",
            description = "Adds a like to the article and returns the updated like status for the viewer."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Article liked successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = NewsLikeStatusResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "The caller is not authenticated",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(implementation = String.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "The targeted article was not found",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(implementation = String.class))
            )
    })
    @PostMapping("/{articleId}/likes")
    public NewsLikeStatusResponse likeNews(@PathVariable Long articleId,
                                           @Parameter(hidden = true)
                                           @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        return newsService.likeArticle(articleId, authenticatedUser);
    }

    /**
     * Removes a like from a news article.
     *
     * @param articleId identifier of the article
     * @param authenticatedUser authenticated user removing the like
     * @return like status response
     */
    @Operation(
            summary = "Remove a like",
            description = "Removes the viewer's like from the specified article and returns the updated like status."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Like removed successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = NewsLikeStatusResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "The caller is not authenticated",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(implementation = String.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "The targeted article was not found",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(implementation = String.class))
            )
    })
    @DeleteMapping("/{articleId}/likes")
    public NewsLikeStatusResponse unlikeNews(@PathVariable Long articleId,
                                             @Parameter(hidden = true)
                                             @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        return newsService.unlikeArticle(articleId, authenticatedUser);
    }

    /**
     * Handles requests for non-existing articles.
     *
     * @param ex exception describing the failure
     * @return 404 response
     */
    @ExceptionHandler(NewsArticleNotFoundException.class)
    public ResponseEntity<String> handleNewsNotFound(NewsArticleNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    /**
     * Handles requests for non-existing comments.
     *
     * @param ex exception describing the failure
     * @return 404 response
     */
    @ExceptionHandler(NewsCommentNotFoundException.class)
    public ResponseEntity<String> handleCommentNotFound(NewsCommentNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    /**
     * Handles validation errors in the request payload or query parameters.
     *
     * @param ex exception describing the validation error
     * @return 400 response
     */
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<String> handleBadRequest(BadRequestException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    /**
     * Handles unauthorized requests.
     *
     * @param ex exception describing the failure
     * @return 401 response
     */
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<String> handleUnauthorized(UnauthorizedException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
    }

    private Pageable buildPageRequest(int page, int size, Sort sort) {
        if (page < 0) {
            throw new BadRequestException("Page index must be greater or equal to 0");
        }
        if (size <= 0) {
            throw new BadRequestException("Page size must be greater than 0");
        }
        int cappedSize = Math.min(size, MAX_PAGE_SIZE);
        return PageRequest.of(page, cappedSize, sort);
    }
}
