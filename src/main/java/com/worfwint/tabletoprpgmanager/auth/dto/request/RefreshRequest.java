package com.worfwint.tabletoprpgmanager.auth.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 * Request payload used to obtain a new access token from a refresh token.
 *
 * @param refreshToken Refresh token issued during a previous authentication flow.
 */
public record RefreshRequest(@NotBlank(message = "Refresh token is required") String refreshToken) {

}
