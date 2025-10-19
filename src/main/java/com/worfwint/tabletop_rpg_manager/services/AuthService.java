package com.worfwint.tabletop_rpg_manager.services;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import com.worfwint.tabletop_rpg_manager.dto.RefreshTokenServiced;
import com.worfwint.tabletop_rpg_manager.dto.request.LoginRequest;
import com.worfwint.tabletop_rpg_manager.dto.request.RegisterRequest;
import com.worfwint.tabletop_rpg_manager.dto.response.AuthResponse;
import com.worfwint.tabletop_rpg_manager.entity.User;
import com.worfwint.tabletop_rpg_manager.entity.UserToken;
import com.worfwint.tabletop_rpg_manager.repository.UserRepository;
import com.worfwint.tabletop_rpg_manager.security.JwtService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

import com.worfwint.tabletop_rpg_manager.repository.UserTokenRepository;

/**
 *
 * @author michael
 */
@Service
@AllArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final UserTokenRepository userTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final TokenCacheService tokenCacheService;

    @Transactional
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // TODO(michael): validate password strength
        // TODO(michael): validate username format
        
        User user = new User(
                request.getUsername(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword())
        );
        
        if (request.getDisplayName() != null && !request.getDisplayName().trim().isEmpty()) {
            user.setDisplayName(request.getDisplayName());
        } else {
            user.setDisplayName(request.getUsername());
        }
        
        user = userRepository.save(user);

        String accessToken = jwtService.generateAccessToken(user.getId());
        RefreshTokenServiced refreshToken = jwtService.generateRefreshToken();
        String packedRefreshToken = jwtService.combineRefreshTokenWithJti(refreshToken.getToken(), refreshToken.getJti());

        userTokenRepository.save(new UserToken(
                refreshToken.getJti(),
                user,
                passwordEncoder.encode(refreshToken.getToken()),
                refreshToken.getExpiresAt()
        ));

        return new AuthResponse(
                accessToken,
                packedRefreshToken,
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getDisplayName()
        );
    }

    public AuthResponse authenticate(@Valid @RequestBody LoginRequest request) {
        User user;
        if (request.getLogin().contains("@")) {
            user = userRepository.findByEmail(request.getLogin())
                    .orElseThrow(() -> new RuntimeException("User not found"));
        } else {
            user = userRepository.findByUsername(request.getLogin())
                    .orElseThrow(() -> new RuntimeException("User not found"));
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid password");
        }

        // TODO(michael): check work
        tokenCacheService.invalidate(user.getId());

        // UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        String accessToken = jwtService.generateAccessToken(user.getId());
        RefreshTokenServiced refreshToken = jwtService.generateRefreshToken();
        String packedRefreshToken = jwtService.combineRefreshTokenWithJti(refreshToken.getToken(), refreshToken.getJti());

        userTokenRepository.save(new UserToken(
                refreshToken.getJti(),
                user,
                passwordEncoder.encode(refreshToken.getToken()),
                refreshToken.getExpiresAt()
        ));
        
        return new AuthResponse(
                accessToken,
                packedRefreshToken,
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getDisplayName()
        );
    }
}
