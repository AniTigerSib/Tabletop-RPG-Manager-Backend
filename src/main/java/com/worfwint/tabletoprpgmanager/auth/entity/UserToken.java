package com.worfwint.tabletoprpgmanager.auth.entity;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

import com.worfwint.tabletoprpgmanager.user.entity.User;
import lombok.Data;
import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;

/**
 * Stores metadata about refresh tokens issued to users.
 */
@Entity
@Table(name = "user_token")
@Audited
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@Data
public class UserToken {

    /**
     * Unique token identifier (JWT ID).
     */
    @Id
    private UUID jti;

    /**
     * Hashed representation of the refresh token.
     */
    @Column(nullable = false)
    private String token;

    /**
     * User who owns this token.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Indicates whether the token has been revoked.
     */
    @Column(nullable = false)
    private boolean revoked = false;

    /**
     * Expiration time of the token.
     */
    @Column(name = "expires_at", nullable = false)
    private Date expiresAt;

    /**
     * Timestamp when the token was revoked, if applicable.
     */
    @Column(name = "revoked_at")
    private Date revokedAt = null;

    /**
     * Timestamp when the token was persisted.
     */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Creates a new token entry bound to the specified user.
     *
     * @param jti unique token identifier
     * @param user owner of the token
     * @param token hashed token value
     * @param expiresAt expiration date of the token
     */
    public UserToken(UUID jti, User user, String token, Date expiresAt) {
        this.jti = jti;
        this.token = token;
        this.user = user;
        this.expiresAt = new Date(expiresAt.getTime()); // Defensive copy for Date
    }

    @Override
    public String toString() {
        return "UserTokens{" +
                "id=" + jti +
                ", token='" + token + '\'' +
                ", user=" + (user != null ? user.getId() : null) +
                ", expiresAt=" + expiresAt +
                ", createdAt=" + createdAt +
                '}';
    }
}
