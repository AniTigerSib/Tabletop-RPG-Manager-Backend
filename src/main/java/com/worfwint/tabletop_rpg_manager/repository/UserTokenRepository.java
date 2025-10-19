package com.worfwint.tabletop_rpg_manager.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

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
}
