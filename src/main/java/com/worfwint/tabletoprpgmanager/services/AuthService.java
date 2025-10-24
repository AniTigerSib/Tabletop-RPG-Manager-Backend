package com.worfwint.tabletoprpgmanager.services;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import com.worfwint.tabletoprpgmanager.dto.TokenPair;
import com.worfwint.tabletoprpgmanager.dto.request.LoginRequest;
import com.worfwint.tabletoprpgmanager.dto.request.RegisterRequest;
import com.worfwint.tabletoprpgmanager.dto.response.AuthResponse;
import com.worfwint.tabletoprpgmanager.entity.User;
import com.worfwint.tabletoprpgmanager.exception.EmailAlreadyExistsException;
import com.worfwint.tabletoprpgmanager.exception.InvalidCredentialsException;
import com.worfwint.tabletoprpgmanager.exception.UsernameAlreadyExistsException;
import com.worfwint.tabletoprpgmanager.repository.UserRepository;
import com.worfwint.tabletoprpgmanager.security.JwtService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

/**
 * Handles authentication workflows such as registration, login, and token refresh.
 */
@Service
@AllArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    /**
     * Registers a new user and issues an initial token pair.
     *
     * @param request registration details supplied by the client
     * @return authentication response containing the issued tokens
     */
    @Transactional
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        if (Boolean.TRUE.equals(userRepository.existsByUsername(request.getUsername()))) {
            throw new UsernameAlreadyExistsException();
        }

        if (Boolean.TRUE.equals(userRepository.existsByEmail(request.getEmail()))) {
            throw new EmailAlreadyExistsException();
        }

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

        TokenPair tokenPair = jwtService.generateTokenPair(user);

        return new AuthResponse(
                tokenPair.getAccessToken(),
                tokenPair.getRefreshToken(),
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getDisplayName()
        );
    }

    /**
     * Authenticates a user using a username/email and password pair.
     *
     * @param request login credentials supplied by the client
     * @return authentication response containing a fresh token pair
     */
    @Transactional
    public AuthResponse authenticate(@Valid @RequestBody LoginRequest request) {
        User user;
        if (request.getLogin().contains("@")) {
            user = userRepository.findByEmail(request.getLogin())
                    .orElseThrow(InvalidCredentialsException::new);
        } else {
            user = userRepository.findByUsername(request.getLogin())
                    .orElseThrow(InvalidCredentialsException::new);
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException("Invalid credentails");
        }

        jwtService.revokeAllTokens(user.getId());
        TokenPair tokenPair = jwtService.generateTokenPair(user);

        return new AuthResponse(
                tokenPair.getAccessToken(),
                tokenPair.getRefreshToken(),
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getDisplayName()
        );
    }

    /**
     * Exchanges a refresh token for a new token pair.
     *
     * @param refreshToken refresh token provided by the client
     * @return authentication response containing a refreshed token pair
     */
    public AuthResponse refreshToken(String refreshToken) {
        refreshToken = refreshToken.trim();
        Long userId = jwtService.extractSubject(refreshToken);

        User user = userRepository.findById(userId)
                .orElseThrow(InvalidCredentialsException::new);

        TokenPair tokenPair = jwtService.refresh(refreshToken, user);

        return new AuthResponse(
                tokenPair.getAccessToken(),
                tokenPair.getRefreshToken(),
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getDisplayName()
        );
    }

    /**
     * Logs out a user by revoking all of their active tokens.
     *
     * @param userId identifier of the user who is logging out
     */
    @Transactional
    public void logout(Long userId) {
        jwtService.revokeAllTokens(userId);
    }
}
