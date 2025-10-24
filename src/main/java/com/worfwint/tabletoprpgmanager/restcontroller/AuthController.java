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

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<Object> register(@Valid @RequestBody RegisterRequest request) {
        try {
            AuthResponse response = authService.register(request);
            return ResponseEntity.ok(response);
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Object> authenticate(@Valid @RequestBody LoginRequest request) {
        try {
            AuthResponse response = authService.authenticate(request);
            return ResponseEntity.ok(response);
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<Object> refreshTokens(@Valid @RequestBody RefreshRequest request) {
        try {
            AuthResponse response = authService.refreshToken(request.getRefreshToken());
            return ResponseEntity.ok(response);
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Object> logout(@AuthenticationPrincipal AuthenticatedUser user) {
        if (user == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        authService.logout(user.getId());
        return ResponseEntity.ok().build();
    }
}
