package com.worfwint.tabletop_rpg_manager.restcontroller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.worfwint.tabletop_rpg_manager.dto.request.LoginRequest;
import com.worfwint.tabletop_rpg_manager.dto.request.RegisterRequest;
import com.worfwint.tabletop_rpg_manager.dto.response.AuthResponse;
import com.worfwint.tabletop_rpg_manager.exception.BadRequestException;
import com.worfwint.tabletop_rpg_manager.exception.UnauthorizedException;
import com.worfwint.tabletop_rpg_manager.services.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        try {
            AuthResponse response = authService.register(request);
            return ResponseEntity.ok(response);
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticate(@Valid @RequestBody LoginRequest request) {
        try {
            AuthResponse response = authService.authenticate(request);
            return ResponseEntity.ok(response);
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(401).build();
        }
    }
}
