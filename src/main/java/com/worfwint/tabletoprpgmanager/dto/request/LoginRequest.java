package com.worfwint.tabletoprpgmanager.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Request payload for authenticating an existing user.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class LoginRequest {

    /**
     * Username or email supplied by the user for authentication.
     */
    @NotBlank(message = "Login is required")
    private String login;

    /**
     * Plain text password supplied during authentication.
     */
    @NotBlank(message = "Password is required")
    private String password;
}
