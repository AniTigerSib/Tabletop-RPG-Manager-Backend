package com.worfwint.tabletoprpgmanager.repository;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.worfwint.tabletoprpgmanager.entity.User;
import com.worfwint.tabletoprpgmanager.entity.UserToken;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author michael
 */
@Repository
public interface UserTokenRepository extends JpaRepository<UserToken, UUID> {

    Optional<UserToken> findByUser(User user);

    Optional<UserToken> findByUserId(Long userId);

    @Modifying
    @Transactional
    @Query("""
        UPDATE UserToken ut
        SET ut.revoked = true,
            ut.revokedAt = :revokedAt
        WHERE ut.user.id = :userId
          AND ut.revoked = false
    """)
    void revokeAllTokensForUser(@Param("userId") Long userId, @Param("revokedAt") Date revokedAt);
}
