package com.worfwint.tabletoprpgmanager.restcontroller;

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

import com.worfwint.tabletoprpgmanager.dto.AuthenticatedUser;
import com.worfwint.tabletoprpgmanager.dto.request.CreateNewsCommentRequest;
import com.worfwint.tabletoprpgmanager.dto.request.CreateNewsRequest;
import com.worfwint.tabletoprpgmanager.dto.request.UpdateNewsCommentRequest;
import com.worfwint.tabletoprpgmanager.dto.request.UpdateNewsRequest;
import com.worfwint.tabletoprpgmanager.dto.response.NewsCommentResponse;
import com.worfwint.tabletoprpgmanager.dto.response.NewsDetailResponse;
import com.worfwint.tabletoprpgmanager.dto.response.NewsLikeStatusResponse;
import com.worfwint.tabletoprpgmanager.dto.response.NewsSummaryResponse;
import com.worfwint.tabletoprpgmanager.dto.response.PageResponse;
import com.worfwint.tabletoprpgmanager.exception.BadRequestException;
import com.worfwint.tabletoprpgmanager.exception.NewsArticleNotFoundException;
import com.worfwint.tabletoprpgmanager.exception.NewsCommentNotFoundException;
import com.worfwint.tabletoprpgmanager.exception.UnauthorizedException;
import com.worfwint.tabletoprpgmanager.services.NewsService;

import jakarta.validation.Valid;

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
    @GetMapping({"", "/"})
    public PageResponse<NewsSummaryResponse> getNews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "" + DEFAULT_NEWS_PAGE_SIZE) int size,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
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
    @GetMapping("/{articleId}")
    public NewsDetailResponse getNewsArticle(@PathVariable Long articleId,
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
    @PreAuthorize("hasAnyRole('MODERATOR','ADMIN','DEVELOPER')")
    @PostMapping({"", "/"})
    public NewsDetailResponse createNews(@Valid @RequestBody CreateNewsRequest request,
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
    @PreAuthorize("hasAnyRole('MODERATOR','ADMIN','DEVELOPER')")
    @PutMapping("/{articleId}")
    public NewsDetailResponse updateNews(@PathVariable Long articleId,
                                         @Valid @RequestBody UpdateNewsRequest request,
                                         @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        return newsService.updateArticle(articleId, authenticatedUser, request);
    }

    /**
     * Deletes a news article.
     *
     * @param articleId identifier of the article
     * @param authenticatedUser authenticated user performing the deletion
     */
    @PreAuthorize("hasAnyRole('MODERATOR','ADMIN','DEVELOPER')")
    @DeleteMapping("/{articleId}")
    public ResponseEntity<Void> deleteNews(@PathVariable Long articleId,
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
    @GetMapping("/{articleId}/comments")
    public PageResponse<NewsCommentResponse> getNewsComments(
            @PathVariable Long articleId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "" + DEFAULT_COMMENT_PAGE_SIZE) int size,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
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
    @PostMapping("/{articleId}/comments")
    public NewsCommentResponse createNewsComment(@PathVariable Long articleId,
                                                 @Valid @RequestBody CreateNewsCommentRequest request,
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
    @PutMapping("/{articleId}/comments/{commentId}")
    public NewsCommentResponse updateNewsComment(@PathVariable Long articleId,
                                                 @PathVariable Long commentId,
                                                 @Valid @RequestBody UpdateNewsCommentRequest request,
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
    @DeleteMapping("/{articleId}/comments/{commentId}")
    public ResponseEntity<Void> deleteNewsComment(@PathVariable Long articleId,
                                                  @PathVariable Long commentId,
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
    @PostMapping("/{articleId}/likes")
    public NewsLikeStatusResponse likeNews(@PathVariable Long articleId,
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
    @DeleteMapping("/{articleId}/likes")
    public NewsLikeStatusResponse unlikeNews(@PathVariable Long articleId,
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
