package com.worfwint.tabletoprpgmanager.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.worfwint.tabletoprpgmanager.entity.User;

/**
 * Repository abstraction for working with {@link User} entities.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by their unique username.
     *
     * @param username value to search for
     * @return the matching {@link User} or {@link Optional#empty()}
     */
    Optional<User> findByUsername(String username);

    /**
     * Finds a user by their unique email address.
     *
     * @param email value to search for
     * @return the matching {@link User} or {@link Optional#empty()}
     */
    Optional<User> findByEmail(String email);

    /**
     * Checks whether a username is already in use.
     *
     * @param username username to check
     * @return {@code true} when the username exists
     */
    Boolean existsByUsername(String username);

    /**
     * Checks whether an email address is already in use.
     *
     * @param email email to check
     * @return {@code true} when the email exists
     */
    Boolean existsByEmail(String email);

    /**
     * Finds users whose username contains the supplied fragment (case insensitive).
     *
     * @param username partial username to search for
     * @return list of users with matching usernames
     */
    List<User> findByUsernameContainingIgnoreCase(String username);

    /**
     * Returns a paged result of users whose username matches the supplied fragment.
     *
     * @param username partial username to search for
     * @param pageable paging information
     * @return a page of users matching the search
     */
    Page<User> findByUsernameContainingIgnoreCase(String username, Pageable pageable);

    /**
     * Returns a paged result of users filtered by both username and email fragments.
     *
     * @param username partial username to search for
     * @param email partial email to search for
     * @param pageable paging information
     * @return a page of users matching the combined filters
     */
    Page<User> findByUsernameContainingIgnoreCaseAndEmailContainingIgnoreCase(
            String username,
            String email,
            Pageable pageable);
}
