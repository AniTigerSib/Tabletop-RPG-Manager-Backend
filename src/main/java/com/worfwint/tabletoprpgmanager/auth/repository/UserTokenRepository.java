package com.worfwint.tabletoprpgmanager.auth.repository;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.worfwint.tabletoprpgmanager.user.entity.User;
import com.worfwint.tabletoprpgmanager.auth.entity.UserToken;

/**
 * Repository for persisting {@link UserToken} entities associated with user sessions.
 */
@Repository
public interface UserTokenRepository extends JpaRepository<UserToken, UUID> {

    /**
     * Retrieves all tokens issued for the provided user.
     *
     * @param user user whose tokens should be returned
     * @return list of tokens belonging to the user
     */
    List<UserToken> findByUser(User user);

    /**
     * Retrieves all tokens issued for the provided user identifier.
     *
     * @param userId user identifier to filter tokens by
     * @return list of tokens belonging to the user
     */
    List<UserToken> findByUserId(Long userId);

    /**
     * Revokes a token with the given JWT identifier if it has not been revoked yet.
     *
     * @param jti      JWT token identifier
     * @param revokedAt timestamp of the revocation
     * @return the number of updated rows
     */
    @Modifying
    @Query("""
        UPDATE UserToken ut
        SET ut.revoked = true,
            ut.revokedAt = :revokedAt
        WHERE ut.jti = :jti
          AND ut.revoked = false
    """)
    int revokeIfNotRevoked(@Param("jti") UUID jti, @Param("revokedAt") Date revokedAt);

    /**
     * Revokes all active tokens for the specified user.
     *
     * @param userId user identifier whose tokens should be revoked
     * @param revokedAt timestamp of the revocation
     */
    @Modifying
    @Query("""
        UPDATE UserToken ut
        SET ut.revoked = true,
            ut.revokedAt = :revokedAt
        WHERE ut.user.id = :userId
          AND ut.revoked = false
    """)
    void revokeAllTokensForUser(@Param("userId") Long userId, @Param("revokedAt") Date revokedAt);
}
