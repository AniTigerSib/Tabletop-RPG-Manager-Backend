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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

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
    @Operation(
            summary = "Register a new account",
            description = "Creates a user account using the provided username, display name, email and password."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Registration succeeded and the user is automatically authenticated",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AuthResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "The registration data failed validation or the username/email already exists",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(implementation = String.class))
            )
    })
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
    @Operation(
            summary = "Authenticate with username or email",
            description = "Authenticates a user using either username or email paired with a password and "
                    + "returns a token pair for future requests."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Authentication succeeded",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AuthResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "The supplied credentials are invalid",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(implementation = String.class))
            )
    })
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
    @Operation(
            summary = "Refresh an access token",
            description = "Exchanges a valid refresh token for a new access and refresh token pair."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Tokens refreshed successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AuthResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "The provided refresh token is invalid or expired",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(implementation = String.class))
            )
    })
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
    @Operation(
            summary = "Log out the current user",
            description = "Revokes all active refresh tokens issued to the authenticated user."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Logout completed successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Void.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "No authenticated user was found in the request context",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(implementation = String.class))
            )
    })
    @PostMapping("/logout")
    public ResponseEntity<Object> logout(
            @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUser user) {
        if (user == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        authService.logout(user.getId());
        return ResponseEntity.ok().build();
    }
}
