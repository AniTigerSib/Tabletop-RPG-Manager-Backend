package com.worfwint.tabletop_rpg_manager.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.NonNull;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.worfwint.tabletop_rpg_manager.dto.TokenPair;
import com.worfwint.tabletop_rpg_manager.entity.User;
import com.worfwint.tabletop_rpg_manager.entity.UserToken;
import com.worfwint.tabletop_rpg_manager.exception.UnauthorizedException;
import com.worfwint.tabletop_rpg_manager.repository.UserTokenRepository;
import com.worfwint.tabletop_rpg_manager.services.TokenCacheService;

import ch.qos.logback.core.subst.Token;
import io.jsonwebtoken.JwtException;

/**
 *
 * @author michael
 */
@Service
@AllArgsConstructor
public class JwtService {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-expiration}")
    private Long accessExpiration;

    @Value("${jwt.refresh-expiration}")
    private Long refreshExpiration;

    @Value("${jwt.refresh-length}")
    private int refreshLength;

    @Value("${jwt.issuer}")
    private String issuer;

    private final SecureRandom secureRandom = new SecureRandom();
    private final PasswordEncoder passwordEncoder;
    private final TokenCacheService tokenCacheService;
    private final UserTokenRepository userTokenRepository;

    // Generating

    public TokenPair generateTokenPair(@NonNull Long userId) {
        final String refresh = generateRefreshToken(userId);
        final String access = generateAccessToken(userId);
        return new TokenPair(access, refresh);
    }

    public TokenPair refresh(@NotBlank String refreshJwt) {
        if (!isRefreshTokenValid(refreshJwt)) {
            throw new UnauthorizedException("Invalid or expired refresh token");
        }

        final UUID jti = extractJti(refreshJwt);
        final Long userId = extractSubject(refreshJwt);

        UserToken token = userTokenRepository.findById(jti)
            .orElseThrow(() -> new UnauthorizedException("Token not found"));

        final String tokenValue = extractClaim(refreshJwt, "token", String.class);

        if (tokenValue == null || !passwordEncoder.matches(tokenValue, token.getToken())) {
            throw new UnauthorizedException("Invalid refresh token");
        }
        
        if (token.isRevoked()) {
            throw new UnauthorizedException("Refresh token already revoked");
        }

        token.setRevoked(true);
        userTokenRepository.save(token);

        final String newRefresh = generateRefreshToken(userId);
        final String newAccess = generateAccessToken(userId);

        return new TokenPair(newAccess, newRefresh);
    }

    public String generateAccessToken(@NonNull Long userId) {
        final String tokenId = UUID.randomUUID().toString();
        final String token = buildAccessToken(tokenId, userId, accessExpiration, Map.of());
        tokenCacheService.saveAccessTokenVersion(userId, tokenId, Duration.ofMillis(accessExpiration));
        return Base64.getEncoder().withoutPadding().encodeToString(token.getBytes());
    }

    public String generateAccessToken(@NonNull Long userId, Map<String, Object> extraClaims) {
        final String tokenId = UUID.randomUUID().toString();
        final String token = buildAccessToken(tokenId, userId, accessExpiration, extraClaims);
        tokenCacheService.saveAccessTokenVersion(userId, tokenId, Duration.ofMillis(accessExpiration));
        return Base64.getEncoder().withoutPadding().encodeToString(token.getBytes());
    }

    private String buildAccessToken(String tokenId, Long userId, long expiration, Map<String, Object> extraClaims) {
        return Jwts.builder()
                    .id(tokenId)
                    .claims(extraClaims)
                    .subject(userId.toString())
                    .issuer(issuer)
                    .issuedAt(new Date(System.currentTimeMillis()))
                    .expiration(new Date(System.currentTimeMillis() + expiration))
                    .signWith(getSignInKey(), Jwts.SIG.HS256)
                    .compact();
    }

    private String generateRefreshToken(@NotNull Long userId) {
        final UUID tokenId = UUID.randomUUID();
        final Date issuedAt = new Date(System.currentTimeMillis());
        final Date expiresAt = new Date(System.currentTimeMillis() + refreshExpiration);
        byte[] randomBytes = new byte[refreshLength];
        secureRandom.nextBytes(randomBytes);
        final String baseToken = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
        final String token = Jwts.builder()
                    .id(tokenId.toString())
                    .claim("token", baseToken)
                    .subject(userId.toString())
                    .issuer(issuer)
                    .issuedAt(issuedAt)
                    .expiration(expiresAt)
                    .signWith(getSignInKey(), Jwts.SIG.HS256)
                    .compact();
        userTokenRepository.saveByUserId(tokenId, userId, passwordEncoder.encode(baseToken), expiresAt);
        return token;
    }

    // Validation

    public boolean isAccessTokenValid(@NotBlank String token) {
        try {
            final Claims claims = extractAllClaims(token);
            final String tokenIssuer = claims.getIssuer();
            final Long subject = Long.valueOf(claims.getSubject());
            return !isTokenExpired(claims)
                   && tokenIssuer.equals(issuer)
                   && tokenCacheService.isValidAccessTokenVersion(subject, claims.getId());
        } catch (JwtException e) {
            return false;
        }
    }

    private boolean isTokenExpired(@NotNull Claims claims) {
        return claims.getExpiration().before(new Date());
    }

    private boolean isRefreshTokenValid(@NotBlank String token) {
        try {
            final Claims claims = extractAllClaims(token);
            final String tokenIssuer = claims.getIssuer();
            return !isTokenExpired(claims) && tokenIssuer.equals(issuer);
        } catch (JwtException e) {
            return false;
        }
    }


    // Service

    private SecretKey getSignInKey() {
        byte[] keyBytes = secret.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private UUID extractJti(@NotBlank String token) {
        try {
            return UUID.fromString(extractClaim(token, Claims::getId));
        } catch (IllegalArgumentException e) {
            throw new UnauthorizedException("Invalid token ID");
        }
    }

    private Long extractSubject(@NotBlank String token) {
        try {
            return extractClaim(token, claims -> Long.valueOf(claims.getSubject()));
        } catch (NumberFormatException e) {
            throw new UnauthorizedException("Invalid token subject");
        }
    }

    private <T> T extractClaim(@NotBlank String token, @NotBlank String claimName, @NotNull Class<T> expectedType) {
        try {
            final Claims claims = extractAllClaims(token);
            Object claimValue = claims.get(claimName);
            if (claimValue == null) {
                return null;
            }
            
            if (!expectedType.isInstance(claimValue)) {
                throw new IllegalArgumentException("Claim value is not of the expected type");
            }
            
            return expectedType.cast(claimValue);
        } catch (JwtException e) {
            throw new UnauthorizedException("Invalid token");
        } catch (IllegalArgumentException e) {
            throw new UnauthorizedException("Invalid claim value");
        }
    }

    private <T> T extractClaim(@NotBlank String token, Function<Claims, T> resolver) {
        final Claims claims = extractAllClaims(token);
        return resolver.apply(claims);
    }

    private Claims extractAllClaims(@NotBlank String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
