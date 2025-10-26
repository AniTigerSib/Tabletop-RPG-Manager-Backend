package com.worfwint.tabletoprpgmanager.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.worfwint.tabletoprpgmanager.entity.NewsComment;

/**
 * Repository for managing news comments.
 */
public interface NewsCommentRepository extends JpaRepository<NewsComment, Long> {

    /**
     * Returns paginated comments for a specific article ordered by newest first.
     *
     * @param articleId identifier of the article
     * @param pageable paging configuration
     * @return page containing comments
     */
    @EntityGraph(attributePaths = "author")
    Page<NewsComment> findByArticleIdOrderByCreatedAtDesc(Long articleId, Pageable pageable);

    /**
     * Retrieves a comment for the given identifiers with the author eager loaded.
     *
     * @param commentId identifier of the comment
     * @param articleId identifier of the article
     * @return optional containing the comment when found
     */
    @EntityGraph(attributePaths = "author")
    java.util.Optional<NewsComment> findByIdAndArticleId(Long commentId, Long articleId);
}
