package com.worfwint.tabletoprpgmanager.news.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.worfwint.tabletoprpgmanager.news.entity.NewsArticle;

/**
 * Repository providing persistence operations for news articles.
 */
public interface NewsArticleRepository extends JpaRepository<NewsArticle, Long> {

    /**
     * Returns a paginated list of news articles ordered by newest first.
     *
     * @param pageable paging configuration
     * @return page containing the requested slice of articles
     */
    @EntityGraph(attributePaths = "author")
    Page<NewsArticle> findAllByOrderByCreatedAtDesc(Pageable pageable);

    /**
     * Retrieves a specific article ensuring the author is eagerly loaded.
     *
     * @param id identifier of the article
     * @return article wrapped in an {@link Optional}
     */
    @Override
    @EntityGraph(attributePaths = "author")
    Optional<NewsArticle> findById(Long id);
}
