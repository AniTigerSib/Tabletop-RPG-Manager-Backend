package com.worfwint.tabletoprpgmanager.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

/**
 * Request payload for authenticating an existing user.
 */

@Getter
public class LoginRequest {

    /**
     * Username or email supplied by the user for authentication.
     */
    @NotBlank(message = "Login is required")
    public String login;

    /**
     * Plain text password supplied during authentication.
     */
    @NotBlank(message = "Password is required")
    public String password;

    /**
     * Constructor for LoginRequest.
     * @param login    Username or email supplied by the user for authentication.
     * @param password Plain text password supplied during authentication.
     */
    LoginRequest(String login, String password) {
        this.login = login;
        this.password = password;
    }

    LoginRequest validate() {
        login = login.trim();
        password = password.trim();
        return this;
    }
}
