package com.worfwint.tabletop_rpg_manager.repository;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.worfwint.tabletop_rpg_manager.entity.User;
import com.worfwint.tabletop_rpg_manager.entity.UserToken;

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
    @Query(
        value = "INSERT INTO user_token (id, user_id, token, expires_at) VALUES (:tokenId, :userId, :token, :expiresAt)",
        nativeQuery = true
    )
    UserToken saveByUserId(
        @Param("tokenId") UUID tokenId,
        @Param("userId") Long userId,
        @Param("token") String token,
        @Param("expiresAt") Date expiresAt);
}
