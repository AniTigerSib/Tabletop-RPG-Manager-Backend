package com.worfwint.tabletoprpgmanager.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Request payload used to obtain a new access token from a refresh token.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class RefreshRequest {

    /**
     * Refresh token issued during a previous authentication flow.
     */
    @NotBlank
    private String refreshToken;
}
