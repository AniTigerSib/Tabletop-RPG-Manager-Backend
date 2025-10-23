package com.worfwint.tabletoprpgmanager.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.worfwint.tabletoprpgmanager.entity.User;
import com.worfwint.tabletoprpgmanager.entity.UserToken;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author michael
 */
@Repository
public interface UserTokenRepository extends JpaRepository<UserToken, UUID> {

    Optional<UserToken> findByUser(User user);

    Optional<UserToken> findByUserId(Long userId);
}
