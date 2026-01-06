package com.worfwint.tabletoprpgmanager.news.repository;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.worfwint.tabletoprpgmanager.news.entity.NewsLike;

/**
 * Repository for managing user likes of news articles.
 */
public interface NewsLikeRepository extends JpaRepository<NewsLike, Long> {

    /**
     * Checks if a like exists for the given article and user.
     *
     * @param articleId article identifier
     * @param userId user identifier
     * @return {@code true} if a like is present, {@code false} otherwise
     */
    boolean existsByArticleIdAndUserId(Long articleId, Long userId);

    /**
     * Counts the number of likes for the given article.
     *
     * @param articleId article identifier
     * @return total likes
     */
    long countByArticleId(Long articleId);

    /**
     * Removes a like for the specified article and user if it exists.
     *
     * @param articleId article identifier
     * @param userId user identifier
     */
    void deleteByArticleIdAndUserId(Long articleId, Long userId);

    /**
     * Returns the like entity for the given article and user.
     *
     * @param articleId article identifier
     * @param userId user identifier
     * @return optional containing the like if present
     */
    Optional<NewsLike> findByArticleIdAndUserId(Long articleId, Long userId);

    /**
     * Returns the identifiers of articles liked by the specified user within a provided set.
     *
     * @param userId identifier of the user
     * @param articleIds collection of article identifiers to filter
     * @return set of article identifiers liked by the user
     */
    @Query("""
        SELECT nl.article.id
        FROM NewsLike nl
        WHERE nl.user.id = :userId AND nl.article.id IN :articleIds
    """)
    Set<Long> findArticleIdsLikedByUser(@Param("userId") Long userId,
                                        @Param("articleIds") Collection<Long> articleIds);
}
