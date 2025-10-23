package com.worfwint.tabletoprpgmanager.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.worfwint.tabletoprpgmanager.entity.User;

/**
 * UserRepository interface implemented via Spring Data JPA.
 * @author michael
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    List<User> findByUsernameContainingIgnoreCase(String username);

    // Page<User> findAll(Pageable pageable);

    Page<User> findByUsernameContainingIgnoreCase(String username, Pageable pageable);

    // Page<User> findByEmailContainingIgnoreCase(String email, Pageable pageable);

    // Page<User> findByRoleAndUsernameContainingIgnoreCase(UserRole role, String username, Pageable pageable);

    Page<User> findByUsernameContainingIgnoreCaseAndEmailContainingIgnoreCase(String username, String email, Pageable pageable);

    // List<User> findAll();

    // void deleteById(Long id);

}
