package com.worfwint.tabletop_rpg_manager.entity;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Class representing user tokens for authentication or session management.
 * @author michael
 */
@Entity
@Table(name = "user_token")
@Audited
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@Getter
@Setter
public class UserToken {
    // @Column(nullable = false, unique = true)
    @Id
    private UUID jti;

    @Column(nullable = false)
    private String token;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private boolean revoked = false;

    @Column(name = "expires_at", nullable = false)
    private Date expiresAt;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Constructors

    public UserToken(UUID jti, User user, String token, Date expiresAt) {
        this.jti = jti;
        this.token = token;
        this.user = user;
        this.expiresAt = expiresAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserToken that = (UserToken) o;

        return jti != null ? jti.equals(that.jti) : that.jti == null;
    }

    @Override
    public int hashCode() {
        return jti != null ? jti.hashCode() : 0;
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
