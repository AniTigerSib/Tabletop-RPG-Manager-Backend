package com.worfwint.tabletoprpgmanager.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Response payload returned after successful authentication or token refresh.
 */
@Schema(description = "Authentication payload containing the issued token pair and user profile data.")
@NoArgsConstructor
@Getter
@Setter
public class AuthResponse {

    /**
     * JWT access token that authorizes future API requests.
     */
    @Schema(description = "JWT access token that authorizes future API requests")
    private String accessToken;

    /**
     * JWT refresh token that can be exchanged for new access tokens.
     */
    @Schema(description = "JWT refresh token that can be exchanged for a new access token")
    private String refreshToken;

    /**
     * The token type identifier used in Authorization headers.
     */
    @Schema(description = "Type of token placed in the Authorization header", example = "Bearer")
    private String tokenType = "Bearer";

    /**
     * Identifier of the authenticated user.
     */
    @Schema(description = "Identifier of the authenticated user")
    private Long userId;

    /**
     * Username of the authenticated user.
     */
    @Schema(description = "Username of the authenticated user")
    private String username;

    /**
     * Email address of the authenticated user.
     */
    @Schema(description = "Email address of the authenticated user")
    private String email;

    /**
     * Display name presented to other users.
     */
    @Schema(description = "Display name presented to other users")
    private String displayName;

    /**
     * Creates a response with the token pair and user profile details.
     *
     * @param accessToken  issued access token
     * @param refreshToken issued refresh token
     * @param userId       identifier of the authenticated user
     * @param username     username of the authenticated user
     * @param email        email of the authenticated user
     * @param displayName  display name to present to the client
     */
    public AuthResponse(String accessToken, String refreshToken, Long userId,
                        String username, String email, String displayName) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.displayName = displayName;
    }
}
