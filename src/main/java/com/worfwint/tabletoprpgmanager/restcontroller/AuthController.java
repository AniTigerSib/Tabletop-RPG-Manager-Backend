package com.worfwint.tabletoprpgmanager.restcontroller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.worfwint.tabletoprpgmanager.dto.AuthenticatedUser;
import com.worfwint.tabletoprpgmanager.dto.request.LoginRequest;
import com.worfwint.tabletoprpgmanager.dto.request.RefreshRequest;
import com.worfwint.tabletoprpgmanager.dto.request.RegisterRequest;
import com.worfwint.tabletoprpgmanager.dto.response.AuthResponse;
import com.worfwint.tabletoprpgmanager.exception.BadRequestException;
import com.worfwint.tabletoprpgmanager.exception.UnauthorizedException;
import com.worfwint.tabletoprpgmanager.services.AuthService;

import jakarta.validation.Valid;

/**
 * REST controller exposing authentication related endpoints.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    /**
     * Creates a new controller with the required {@link AuthService}.
     *
     * @param authService service handling authentication flows
     */
    AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Registers a new user account.
     *
     * @param request registration details
     * @return {@link AuthResponse} on success or validation errors on failure
     */
    @PostMapping("/register")
    public ResponseEntity<Object> register(@Valid @RequestBody RegisterRequest request) {
        try {
            AuthResponse response = authService.register(request);
            return ResponseEntity.ok(response);
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Authenticates a user using username/email and password credentials.
     *
     * @param request login credentials
     * @return {@link AuthResponse} on success or 401 on failure
     */
    @PostMapping("/login")
    public ResponseEntity<Object> authenticate(@Valid @RequestBody LoginRequest request) {
        try {
            AuthResponse response = authService.authenticate(request);
            return ResponseEntity.ok(response);
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }

    /**
     * Exchanges a refresh token for a new token pair.
     *
     * @param request payload containing the refresh token
     * @return {@link AuthResponse} on success or 401 when the token is invalid
     */
    @PostMapping("/refresh")
    public ResponseEntity<Object> refreshTokens(@Valid @RequestBody RefreshRequest request) {
        try {
            AuthResponse response = authService.refreshToken(request.getRefreshToken());
            return ResponseEntity.ok(response);
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }

    /**
     * Revokes all active tokens for the authenticated user.
     *
     * @param user current authenticated user
     * @return {@code 200 OK} when logout succeeds or {@code 401} if no user is present
     */
    @PostMapping("/logout")
    public ResponseEntity<Object> logout(@AuthenticationPrincipal AuthenticatedUser user) {
        if (user == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        authService.logout(user.getId());
        return ResponseEntity.ok().build();
    }
}
