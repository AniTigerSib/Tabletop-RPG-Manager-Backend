package com.worfwint.tabletoprpgmanager.news.service;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.worfwint.tabletoprpgmanager.common.dto.AuthenticatedUser;
import com.worfwint.tabletoprpgmanager.news.dto.request.CreateNewsCommentRequest;
import com.worfwint.tabletoprpgmanager.news.dto.request.CreateNewsRequest;
import com.worfwint.tabletoprpgmanager.news.dto.request.UpdateNewsCommentRequest;
import com.worfwint.tabletoprpgmanager.news.dto.request.UpdateNewsRequest;
import com.worfwint.tabletoprpgmanager.news.dto.response.NewsAuthorResponse;
import com.worfwint.tabletoprpgmanager.news.dto.response.NewsCommentResponse;
import com.worfwint.tabletoprpgmanager.news.dto.response.NewsDetailResponse;
import com.worfwint.tabletoprpgmanager.news.dto.response.NewsLikeStatusResponse;
import com.worfwint.tabletoprpgmanager.news.dto.response.NewsSummaryResponse;
import com.worfwint.tabletoprpgmanager.common.dto.response.PageResponse;
import com.worfwint.tabletoprpgmanager.news.entity.NewsArticle;
import com.worfwint.tabletoprpgmanager.news.entity.NewsComment;
import com.worfwint.tabletoprpgmanager.news.entity.NewsLike;
import com.worfwint.tabletoprpgmanager.user.entity.User;
import com.worfwint.tabletoprpgmanager.user.entity.UserRole;
import com.worfwint.tabletoprpgmanager.news.exception.NewsArticleNotFoundException;
import com.worfwint.tabletoprpgmanager.news.exception.NewsCommentNotFoundException;
import com.worfwint.tabletoprpgmanager.common.exception.UnauthorizedException;
import com.worfwint.tabletoprpgmanager.storage.S3StorageService;
import com.worfwint.tabletoprpgmanager.user.exception.UserNotFoundException;
import com.worfwint.tabletoprpgmanager.news.repository.NewsArticleRepository;
import com.worfwint.tabletoprpgmanager.news.repository.NewsCommentRepository;
import com.worfwint.tabletoprpgmanager.news.repository.NewsLikeRepository;
import com.worfwint.tabletoprpgmanager.user.repository.UserRepository;

/**
 * Service encapsulating business logic for news articles, comments and likes.
 */
@Service
@Transactional
public class NewsService {

    private final NewsArticleRepository newsArticleRepository;
    private final NewsCommentRepository newsCommentRepository;
    private final NewsLikeRepository newsLikeRepository;
    private final UserRepository userRepository;
    private final S3StorageService storageService;

    public NewsService(NewsArticleRepository newsArticleRepository,
                       NewsCommentRepository newsCommentRepository,
                       NewsLikeRepository newsLikeRepository,
                       UserRepository userRepository,
                       S3StorageService storageService) {
        this.newsArticleRepository = newsArticleRepository;
        this.newsCommentRepository = newsCommentRepository;
        this.newsLikeRepository = newsLikeRepository;
        this.userRepository = userRepository;
        this.storageService = storageService;
    }

    /**
     * Returns paginated news summaries ordered by newest first.
     *
     * @param pageable pagination configuration
     * @param currentUser authenticated user, may be {@code null}
     * @return paginated response of summaries
     */
    @Transactional(readOnly = true)
    public PageResponse<NewsSummaryResponse> listArticles(Pageable pageable, AuthenticatedUser currentUser) {
        Page<NewsArticle> newsPage = newsArticleRepository.findAllByOrderByCreatedAtDesc(pageable);

        Set<Long> likedArticleIds = resolveLikedArticleIds(currentUser, extractArticleIds(newsPage));

        return PageResponse.from(newsPage.map(article -> mapToNewsSummary(article,
                likedArticleIds.contains(article.getId()))));
    }

    /**
     * Retrieves a news article by identifier.
     *
     * @param articleId identifier of the article
     * @param currentUser authenticated user, may be {@code null}
     * @return detailed article response
     */
    @Transactional(readOnly = true)
    public NewsDetailResponse getArticle(Long articleId, AuthenticatedUser currentUser) {
        NewsArticle article = newsArticleRepository.findById(articleId)
                .orElseThrow(NewsArticleNotFoundException::new);
        boolean likedByCurrentUser = currentUser != null
                && newsLikeRepository.existsByArticleIdAndUserId(articleId, currentUser.id());
        return mapToNewsDetail(article, likedByCurrentUser);
    }

    /**
     * Creates a news article on behalf of the authenticated user.
     *
     * @param currentUser authenticated user creating the article
     * @param request payload containing article data
     * @return created article response
     */
    public NewsDetailResponse createArticle(AuthenticatedUser currentUser, CreateNewsRequest request) {
        User author = requireUser(currentUser);
        if (!hasEditorialPrivileges(author)) {
            throw new UnauthorizedException("Not allowed to create news articles");
        }
        NewsArticle article = new NewsArticle();
        article.setAuthor(author);
        article.setTitle(request.getTitle().trim());
        article.setSummary(trimToNull(request.getSummary()));
        article.setContent(request.getContent().trim());

        NewsArticle saved = newsArticleRepository.save(article);
        return mapToNewsDetail(saved, false);
    }

    /**
     * Updates the specified news article.
     *
     * @param articleId identifier of the article to update
     * @param currentUser authenticated user attempting the update
     * @param request payload with new article data
     * @return updated article response
     */
    public NewsDetailResponse updateArticle(Long articleId,
                                            AuthenticatedUser currentUser,
                                            UpdateNewsRequest request) {
        NewsArticle article = newsArticleRepository.findById(articleId)
                .orElseThrow(NewsArticleNotFoundException::new);
        User actor = requireUser(currentUser);
        ensureArticleModificationAllowed(actor, article);

        article.setTitle(request.getTitle().trim());
        article.setSummary(trimToNull(request.getSummary()));
        article.setContent(request.getContent().trim());

        return mapToNewsDetail(article, newsLikeRepository.existsByArticleIdAndUserId(articleId, actor.getId()));
    }

    /**
     * Uploads an image for the specified news article.
     *
     * @param articleId identifier of the article to update
     * @param currentUser authenticated user attempting the update
     * @param file image file to upload
     * @return updated article response
     */
    public NewsDetailResponse uploadArticleImage(Long articleId,
                                                 AuthenticatedUser currentUser,
                                                 MultipartFile file) {
        NewsArticle article = newsArticleRepository.findById(articleId)
                .orElseThrow(NewsArticleNotFoundException::new);
        User actor = requireUser(currentUser);
        ensureArticleModificationAllowed(actor, article);

        if (article.getImageUrl() != null && !article.getImageUrl().isBlank()) {
            storageService.deleteByPublicUrl(article.getImageUrl());
        }
        String imageUrl = storageService.uploadNewsImage(articleId, file);
        article.setImageUrl(imageUrl);

        return mapToNewsDetail(article, newsLikeRepository.existsByArticleIdAndUserId(articleId, actor.getId()));
    }

    /**
     * Deletes the image for the specified news article.
     *
     * @param articleId identifier of the article to update
     * @param currentUser authenticated user attempting the update
     * @return updated article response
     */
    public NewsDetailResponse deleteArticleImage(Long articleId, AuthenticatedUser currentUser) {
        NewsArticle article = newsArticleRepository.findById(articleId)
                .orElseThrow(NewsArticleNotFoundException::new);
        User actor = requireUser(currentUser);
        ensureArticleModificationAllowed(actor, article);

        if (article.getImageUrl() != null && !article.getImageUrl().isBlank()) {
            storageService.deleteByPublicUrl(article.getImageUrl());
            article.setImageUrl(null);
        }

        return mapToNewsDetail(article, newsLikeRepository.existsByArticleIdAndUserId(articleId, actor.getId()));
    }

    /**
     * Deletes the specified news article.
     *
     * @param articleId identifier of the article to delete
     * @param currentUser authenticated user attempting the deletion
     */
    public void deleteArticle(Long articleId, AuthenticatedUser currentUser) {
        NewsArticle article = newsArticleRepository.findById(articleId)
                .orElseThrow(NewsArticleNotFoundException::new);
        User actor = requireUser(currentUser);
        ensureArticleModificationAllowed(actor, article);
        newsArticleRepository.delete(article);
    }

    /**
     * Returns paginated comments for the given article.
     *
     * @param articleId identifier of the article
     * @param pageable pagination configuration
     * @param currentUser authenticated user, may be {@code null}
     * @return paginated comment response
     */
    @Transactional(readOnly = true)
    public PageResponse<NewsCommentResponse> listComments(Long articleId,
                                                          Pageable pageable,
                                                          AuthenticatedUser currentUser) {
        if (!newsArticleRepository.existsById(articleId)) {
            throw new NewsArticleNotFoundException();
        }
        Page<NewsComment> comments = newsCommentRepository.findByArticleIdOrderByCreatedAtDesc(articleId, pageable);
        Long currentUserId = currentUser != null ? currentUser.id() : null;
        return PageResponse.from(comments.map(comment ->
                mapToNewsComment(comment, currentUserId != null && currentUserId.equals(comment.getAuthor().getId()))));
    }

    /**
     * Creates a new comment on a news article.
     *
     * @param articleId identifier of the article
     * @param currentUser authenticated user leaving the comment
     * @param request comment payload
     * @return created comment response
     */
    public NewsCommentResponse createComment(Long articleId,
                                             AuthenticatedUser currentUser,
                                             CreateNewsCommentRequest request) {
        NewsArticle article = newsArticleRepository.findById(articleId)
                .orElseThrow(NewsArticleNotFoundException::new);
        User author = requireUser(currentUser);

        NewsComment comment = new NewsComment();
        comment.setArticle(article);
        comment.setAuthor(author);
        comment.setContent(request.getContent().trim());

        NewsComment saved = newsCommentRepository.save(comment);
        return mapToNewsComment(saved, true);
    }

    /**
     * Updates an existing comment.
     *
     * @param articleId identifier of the article
     * @param commentId identifier of the comment
     * @param currentUser authenticated user attempting the update
     * @param request payload with new content
     * @return updated comment response
     */
    public NewsCommentResponse updateComment(Long articleId,
                                             Long commentId,
                                             AuthenticatedUser currentUser,
                                             UpdateNewsCommentRequest request) {
        NewsComment comment = newsCommentRepository.findByIdAndArticleId(commentId, articleId)
                .orElseThrow(NewsCommentNotFoundException::new);
        User actor = requireUser(currentUser);
        ensureCommentModificationAllowed(actor, comment);

        comment.setContent(request.getContent().trim());
        boolean ownedByCurrentUser = actor.getId().equals(comment.getAuthor().getId());
        return mapToNewsComment(comment, ownedByCurrentUser);
    }

    /**
     * Deletes a comment from the specified article.
     *
     * @param articleId identifier of the article
     * @param commentId identifier of the comment
     * @param currentUser authenticated user attempting the deletion
     */
    public void deleteComment(Long articleId,
                              Long commentId,
                              AuthenticatedUser currentUser) {
        NewsComment comment = newsCommentRepository.findByIdAndArticleId(commentId, articleId)
                .orElseThrow(NewsCommentNotFoundException::new);
        User actor = requireUser(currentUser);
        ensureCommentModificationAllowed(actor, comment);
        newsCommentRepository.delete(comment);
    }

    /**
     * Adds a like for the specified article if not already present.
     *
     * @param articleId identifier of the article
     * @param currentUser authenticated user performing the action
     * @return updated like status response
     */
    public NewsLikeStatusResponse likeArticle(Long articleId, AuthenticatedUser currentUser) {
        NewsArticle article = newsArticleRepository.findById(articleId)
                .orElseThrow(NewsArticleNotFoundException::new);
        User user = requireUser(currentUser);

        if (!newsLikeRepository.existsByArticleIdAndUserId(article.getId(), user.getId())) {
            NewsLike like = new NewsLike();
            like.setArticle(article);
            like.setUser(user);
            newsLikeRepository.save(like);
        }
        long likeCount = newsLikeRepository.countByArticleId(article.getId());
        return new NewsLikeStatusResponse(likeCount, true);
    }

    /**
     * Removes a like for the specified article if present.
     *
     * @param articleId identifier of the article
     * @param currentUser authenticated user performing the action
     * @return updated like status response
     */
    public NewsLikeStatusResponse unlikeArticle(Long articleId, AuthenticatedUser currentUser) {
        NewsArticle article = newsArticleRepository.findById(articleId)
                .orElseThrow(NewsArticleNotFoundException::new);
        User user = requireUser(currentUser);

        if (newsLikeRepository.existsByArticleIdAndUserId(article.getId(), user.getId())) {
            newsLikeRepository.deleteByArticleIdAndUserId(article.getId(), user.getId());
        }
        long likeCount = newsLikeRepository.countByArticleId(article.getId());
        return new NewsLikeStatusResponse(likeCount, false);
    }

    private Set<Long> resolveLikedArticleIds(AuthenticatedUser currentUser, Collection<Long> articleIds) {
        if (currentUser == null || articleIds.isEmpty()) {
            return Collections.emptySet();
        }
        return newsLikeRepository.findArticleIdsLikedByUser(currentUser.id(), articleIds);
    }

    private Set<Long> extractArticleIds(Page<NewsArticle> articles) {
        return articles.getContent().stream()
                .map(NewsArticle::getId)
                .collect(Collectors.toSet());
    }

    private NewsSummaryResponse mapToNewsSummary(NewsArticle article, boolean likedByCurrentUser) {
        return new NewsSummaryResponse(
                article.getId(),
                article.getTitle(),
                article.getSummary(),
                article.getCreatedAt(),
                article.getUpdatedAt(),
                article.getLikeCount(),
                article.getCommentCount(),
                likedByCurrentUser,
                mapToAuthor(article.getAuthor())
        );
    }

    private NewsDetailResponse mapToNewsDetail(NewsArticle article, boolean likedByCurrentUser) {
        return new NewsDetailResponse(
                article.getId(),
                article.getTitle(),
                article.getSummary(),
                article.getImageUrl(),
                article.getContent(),
                article.getCreatedAt(),
                article.getUpdatedAt(),
                article.getLikeCount(),
                article.getCommentCount(),
                likedByCurrentUser,
                mapToAuthor(article.getAuthor())
        );
    }

    private NewsCommentResponse mapToNewsComment(NewsComment comment, boolean ownedByCurrentUser) {
        return new NewsCommentResponse(
                comment.getId(),
                comment.getContent(),
                comment.getCreatedAt(),
                comment.getUpdatedAt(),
                ownedByCurrentUser,
                mapToAuthor(comment.getAuthor())
        );
    }

    private NewsAuthorResponse mapToAuthor(User user) {
        return new NewsAuthorResponse(
                user.getId(),
                user.getUsername(),
                user.getDisplayName(),
                user.getAvatarUrl()
        );
    }

    private User requireUser(AuthenticatedUser currentUser) {
        if (currentUser == null) {
            throw new UnauthorizedException("Authentication required");
        }
        return userRepository.findById(currentUser.id())
                .orElseThrow(UserNotFoundException::new);
    }

    private void ensureArticleModificationAllowed(User user, NewsArticle article) {
        if (article.getAuthor().getId().equals(user.getId())) {
            return;
        }
        if (hasEditorialPrivileges(user)) {
            return;
        }
        throw new UnauthorizedException("Not allowed to modify this article");
    }

    private void ensureCommentModificationAllowed(User user, NewsComment comment) {
        if (comment.getAuthor().getId().equals(user.getId())) {
            return;
        }
        if (hasEditorialPrivileges(user)) {
            return;
        }
        throw new UnauthorizedException("Not allowed to modify this comment");
    }

    private boolean hasEditorialPrivileges(User user) {
        return user.getRoles().contains(UserRole.ADMIN)
                || user.getRoles().contains(UserRole.MODERATOR)
                || user.getRoles().contains(UserRole.DEVELOPER);
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
