package com.worfwint.tabletoprpgmanager.entity;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.worfwint.tabletoprpgmanager.entity.enums.UserRole;

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
 * Entity representing a user in the system. Audited for tracking changes over time.
 * @author michael
 */
@Entity
@Table(name = "users")
@Audited
@EntityListeners(AuditingEntityListener.class)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

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

    @Column(name = "display_name")
    private String displayName;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<UserToken> tokens = new HashSet<>();

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors

    public User() {}

    public User(String username, String email, String passwordHash) {
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Set<UserRole> getRoles() {
        return Collections.unmodifiableSet(roles);
    }
    public void setRoles(Set<UserRole> roles) {
        if (roles == null || roles.isEmpty()) {
            this.roles = EnumSet.of(UserRole.USER);
        } else {
            this.roles = EnumSet.copyOf(roles);
            ensureDefaultRolePresent();
        }
    }
    public void addRole(UserRole role) {
        if (role != null) {
            this.roles.add(role);
        }
    }
    public void removeRole(UserRole role) {
        if (role == null) {
            return;
        }
        this.roles.remove(role);
        ensureDefaultRolePresent();
    }

    public Set<String> getRoleNames() {
        return roles.stream()
                .map(UserRole::name)
                .collect(Collectors.toUnmodifiableSet());
    }

    public String getDisplayName() {
        return displayName;
    }
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getBio() {
        return bio;
    }
    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }
    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public Set<UserToken> getTokens() {
        return Set.copyOf(tokens);
    }
    public void setTokens(Set<UserToken> tokens) {
        if (tokens == null || tokens.isEmpty()) {
            this.tokens = new HashSet<>();
        } else {
            this.tokens = new HashSet<>(tokens);
        }
    }
    public void addToken(UserToken token) {
        if (token != null) {
            this.tokens.add(token);
            token.setUser(this);
        }
    }
    public void removeToken(UserToken token) {
        if (token != null) {
            this.tokens.remove(token);
            token.setUser(null);
        }
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @PrePersist
    @PreUpdate
    private void ensureRoleIntegrity() {
        if (roles == null || roles.isEmpty()) {
            roles = EnumSet.of(UserRole.USER);
        } else {
            ensureDefaultRolePresent();
        }
    }

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
