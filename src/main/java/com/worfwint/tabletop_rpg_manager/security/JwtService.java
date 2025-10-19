package com.worfwint.tabletop_rpg_manager.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.worfwint.tabletop_rpg_manager.dto.RefreshTokenServiced;
import com.worfwint.tabletop_rpg_manager.services.TokenCacheService;

/**
 *
 * @author michael
 */
@Service
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
    private final TokenCacheService tokenCacheService;

    public JwtService(TokenCacheService tokenCacheService) {
        this.tokenCacheService = tokenCacheService;
    }

    ////////////////////////////////////////////////////
    /// Generating

    public String generateAccessToken(Long userId) {
        final String tokenId = UUID.randomUUID().toString();
        final String token = buildAccessToken(userId, tokenId, Map.of(), accessExpiration);
        tokenCacheService.saveAccessTokenVersion(userId, tokenId, Duration.ofMillis(accessExpiration));
        return token;
    }

    public String generateAccessToken(Long userId, Map<String, Object> extraClaims) {
        final String tokenId = UUID.randomUUID().toString();
        final String token = buildAccessToken(userId, tokenId, extraClaims, accessExpiration);
        tokenCacheService.saveAccessTokenVersion(userId, tokenId, Duration.ofMillis(accessExpiration));
        return token;
    }

    private String buildAccessToken(Long userId, String tokenId, Map<String, Object> extraClaims, long expiration) {
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

    public String combineRefreshTokenWithJti(String refreshToken, UUID jti) {
        return Base64.getEncoder().withoutPadding().encodeToString((jti.toString() + ":" + refreshToken).getBytes());
    }

    public RefreshTokenServiced unpackRefreshToken(String packedToken) {
        byte[] decodedBytes = Base64.getDecoder().decode(packedToken);
        String decodedString = new String(decodedBytes);
        String[] parts = decodedString.split(":", 2);
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid refresh token format");
        }
        UUID jti = UUID.fromString(parts[0]);
        String token = parts[1];
        return new RefreshTokenServiced(jti, token, new Date());
    }

    public RefreshTokenServiced generateRefreshToken() {
        byte[] randomBytes = new byte[refreshLength];
        secureRandom.nextBytes(randomBytes);
        return new RefreshTokenServiced(
            UUID.randomUUID(),
            Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes),
            new Date(System.currentTimeMillis() + refreshExpiration)
        );
    }

    ////////////////////////////////////////////////////
    /// Validation

    public boolean isAccessTokenValid(String token) {
        final Claims claims = extractAllClaims(token);
        final String token_issuer = claims.getIssuer();
        final Long userId = Long.parseLong(claims.getSubject());
        return !isAccessTokenExpired(claims) && tokenCacheService.isValidAccessTokenVersion(userId, claims.getId()) && token_issuer.equals(issuer);
    }

    public boolean isAccessTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private boolean isAccessTokenExpired(Claims claims) {
        return claims.getExpiration().before(new Date());
    }

    ////////////////////////////////////////////////////
    /// Service

    private SecretKey getSignInKey() {
        byte[] keyBytes = secret.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public String extractSubject(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractJti(String token) {
        return extractClaim(token, Claims::getId);
    }

    public <T> T extractClaim(String token, String claimName, Class<T> expectedType) {
        final Claims claims = extractAllClaims(token);
        Object claimValue = claims.get(claimName);
        if (claimValue == null) {
            return null;
        }
        
        if (!expectedType.isInstance(claimValue)) {
            throw new IllegalArgumentException(
                String.format("Claim value is not of the expected type"));
        }
        
        return expectedType.cast(claimValue);
    }
    
    // private String extractIssuer(String token) {
    //     return extractClaim(token, Claims::getIssuer);
    // }

    private <T> T extractClaim(String token, Function<Claims, T> resolver) {
        final Claims claims = extractAllClaims(token);
        return resolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
