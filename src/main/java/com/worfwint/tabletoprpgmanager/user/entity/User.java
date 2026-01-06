package com.worfwint.tabletoprpgmanager.user.entity;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.worfwint.tabletoprpgmanager.auth.entity.UserToken;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

/**
 * Entity representing an application user along with profile and security information.
 * Auditing is enabled to track creation and update timestamps.
 */
@Entity
@Table(name = "users")
@Audited
@EntityListeners(AuditingEntityListener.class)
public class User {

    @Setter
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Getter
    @Column(nullable = false, unique = true)
    private String username;

    @Setter
    @Getter
    @Column(nullable = false, unique = true)
    private String email;

    @Setter
    @Getter
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    // TODO: implement in future
    // @Column(name = "oauth_provider")
    // private String oauthProvider;

    @ElementCollection(fetch = FetchType.EAGER, targetClass = UserRole.class)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "roles")
    private Set<UserRole> roles = EnumSet.of(UserRole.USER);

    @Setter
    @Getter
    @Column(name = "display_name")
    private String displayName;

    @Setter
    @Getter
    @Column(columnDefinition = "TEXT")
    private String bio;

    @Setter
    @Getter
    @Column(name = "avatar_url")
    private String avatarUrl;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<UserToken> tokens = new HashSet<>();

    @Setter
    @Getter
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Setter
    @Getter
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;


    /**
     * Default constructor required by JPA.
     */
    public User() {}

    /**
     * Creates a new user with the mandatory authentication fields populated.
     *
     * @param username unique username for the user
     * @param email email address associated with the account
     * @param passwordHash encoded password hash
     */
    public User(String username, String email, String passwordHash) {
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
    }

    // Getters and Setters

    /**
     * Returns the roles assigned to the user.
     *
     * @return immutable set of roles
     */
    public Set<UserRole> getRoles() {
        return Collections.unmodifiableSet(roles);
    }
    /**
     * Replaces the user's roles, ensuring the default user role is present.
     *
     * @param roles new role set
     */
    public void setRoles(Set<UserRole> roles) {
        if (roles == null || roles.isEmpty()) {
            this.roles = EnumSet.of(UserRole.USER);
        } else {
            this.roles = EnumSet.copyOf(roles);
            ensureDefaultRolePresent();
        }
    }
    /**
     * Adds a role to the user if it is not {@code null}.
     *
     * @param role role to add
     */
    public void addRole(UserRole role) {
        if (role != null) {
            this.roles.add(role);
        }
    }
    /**
     * Removes a role from the user while keeping the default role.
     *
     * @param role role to remove
     */
    public void removeRole(UserRole role) {
        if (role == null) {
            return;
        }
        this.roles.remove(role);
        ensureDefaultRolePresent();
    }

    /**
     * @return immutable set of role names assigned to the user
     */
    public Set<String> getRoleNames() {
        return roles.stream()
                .map(UserRole::name)
                .collect(Collectors.toUnmodifiableSet());
    }

    /**
     * Returns tokens associated with the user.
     *
     * @return immutable copy of stored tokens
     */
    public Set<UserToken> getTokens() {
        return Set.copyOf(tokens);
    }
    /**
     * Replaces the set of tokens associated with the user.
     *
     * @param tokens new token set
     */
    public void setTokens(Set<UserToken> tokens) {
        if (tokens == null || tokens.isEmpty()) {
            this.tokens = new HashSet<>();
        } else {
            this.tokens = new HashSet<>(tokens);
        }
    }
    /**
     * Adds a token and establishes the bidirectional relationship.
     *
     * @param token token to add
     */
    public void addToken(UserToken token) {
        if (token != null) {
            this.tokens.add(token);
            token.setUser(this);
        }
    }
    /**
     * Removes a token and clears the reverse association.
     *
     * @param token token to remove
     */
    public void removeToken(UserToken token) {
        if (token != null) {
            this.tokens.remove(token);
            token.setUser(null);
        }
    }

    /**
     * Ensures the user always has at least the default role before persisting.
     */
    @PrePersist
    @PreUpdate
    private void ensureRoleIntegrity() {
        if (roles == null || roles.isEmpty()) {
            roles = EnumSet.of(UserRole.USER);
        } else {
            ensureDefaultRolePresent();
        }
    }

    /**
     * Ensures the {@link UserRole#USER} role is present in the role set.
     */
    private void ensureDefaultRolePresent() {
        if (!roles.contains(UserRole.USER)) {
            roles.add(UserRole.USER);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        return id != null ? id.equals(user.id) : user.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", role=" + roles.toString() +
                ", displayName='" + displayName + '\'' +
                ", bio='" + bio + '\'' +
                ", avatarUrl='" + avatarUrl + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
