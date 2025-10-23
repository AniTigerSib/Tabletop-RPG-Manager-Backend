package com.worfwint.tabletoprpgmanager.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.worfwint.tabletoprpgmanager.dto.TokenPair;
import com.worfwint.tabletoprpgmanager.entity.User;
import com.worfwint.tabletoprpgmanager.entity.UserToken;
import com.worfwint.tabletoprpgmanager.exception.UnauthorizedException;
import com.worfwint.tabletoprpgmanager.repository.UserTokenRepository;
import com.worfwint.tabletoprpgmanager.services.TokenCacheService;

import io.jsonwebtoken.JwtException;

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
    private final PasswordEncoder passwordEncoder;
    private final TokenCacheService tokenCacheService;
    private final UserTokenRepository userTokenRepository;

    public JwtService(PasswordEncoder passwordEncoder, TokenCacheService tokenCacheService, UserTokenRepository userTokenRepository) {
        this.passwordEncoder = passwordEncoder;
        this.tokenCacheService = tokenCacheService;
        this.userTokenRepository = userTokenRepository;
    }

    // Generating

    public TokenPair generateTokenPair(User user) {
        final String refresh = generateRefreshToken(user);
        final String access = generateAccessToken(user);
        return new TokenPair(access, refresh);
    }

    public TokenPair refresh(String refreshJwt, User user) {
        if (!isRefreshTokenValid(refreshJwt)) {
            throw new UnauthorizedException("Invalid or expired refresh token");
        }

        final UUID jti = extractJti(refreshJwt);

        UserToken token = userTokenRepository.findById(jti)
            .orElseThrow(() -> new UnauthorizedException("Token not found"));

        final String tokenValue = extractClaim(refreshJwt, "token", String.class);

        if (tokenValue == null || !passwordEncoder.matches(tokenValue, token.getToken())) {
            throw new UnauthorizedException("Invalid refresh token");
        }

        if (token.getExpiresAt().before(new Date())) {
            throw new UnauthorizedException("Refresh token expired");
        }
        
        if (token.isRevoked()) {
            throw new UnauthorizedException("Refresh token already revoked");
        }

        token.setRevoked(true);
        userTokenRepository.save(token);

        final String newRefresh = generateRefreshToken(user);
        final String newAccess = generateAccessToken(user);

        return new TokenPair(newAccess, newRefresh);
    }

    public String generateAccessToken(User user) {
        final String tokenId = UUID.randomUUID().toString();
        final String token = buildAccessToken(tokenId, user.getId(), accessExpiration, Map.of(
            "username", user.getUsername(),
            "email", user.getEmail(),
            "roles", new ArrayList<>(user.getRoleNames())
        ));
        tokenCacheService.saveAccessTokenVersion(user.getId(), tokenId, Duration.ofMillis(accessExpiration));
        return token;
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

    private String generateRefreshToken(User user) {
        final UUID tokenId = UUID.randomUUID();
        final Date issuedAt = new Date(System.currentTimeMillis());
        final Date expiresAt = new Date(System.currentTimeMillis() + refreshExpiration);
        byte[] randomBytes = new byte[refreshLength];
        secureRandom.nextBytes(randomBytes);
        final String baseToken = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
        final String token = Jwts.builder()
                    .id(tokenId.toString())
                    .claim("token", baseToken)
                    .subject(user.getId().toString())
                    .issuer(issuer)
                    .issuedAt(issuedAt)
                    .expiration(expiresAt)
                    .signWith(getSignInKey(), Jwts.SIG.HS256)
                    .compact();
        userTokenRepository.save(new UserToken(tokenId, user, passwordEncoder.encode(baseToken), expiresAt));
        return token;
    }

    // Invalidating

    public void revokeAllTokens(Long userId) {
        tokenCacheService.invalidate(userId);
        userTokenRepository.revokeAllTokensForUser(userId, new Date());
    }

    // Validation

    public boolean isAccessTokenValid(String token) {
        try {
            final Claims claims = extractAllClaims(token);
            final String tokenIssuer = claims.getIssuer();
            final Long subject = Long.valueOf(claims.getSubject());
            return !isTokenExpired(claims)
                   && tokenIssuer.equals(issuer)
                   && tokenCacheService.isValidAccessTokenVersion(subject, claims.getId());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        } 
    }

    private boolean isTokenExpired(Claims claims) {
        return claims.getExpiration().before(new Date());
    }

    private boolean isRefreshTokenValid(String token) {
        try {
            final Claims claims = extractAllClaims(token);
            final String tokenIssuer = claims.getIssuer();
            return !isTokenExpired(claims) && tokenIssuer.equals(issuer);
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }


    // Service

    private SecretKey getSignInKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private UUID extractJti(String token) {
        try {
            return UUID.fromString(extractClaim(token, Claims::getId));
        } catch (JwtException | IllegalArgumentException e) {
            throw new UnauthorizedException("Invalid token ID");
        }
    }

    public Long extractSubject(String token) {
        try {
            return extractClaim(token, claims -> Long.valueOf(claims.getSubject()));
        } catch (JwtException | IllegalArgumentException e) {
            throw new UnauthorizedException("Invalid token subject");
        }
    }

    public <T> T extractClaim(String token, String claimName, Class<T> expectedType) {
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
        } catch (JwtException | IllegalArgumentException e) {
            throw new UnauthorizedException("Invalid token");
        }
    }

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
