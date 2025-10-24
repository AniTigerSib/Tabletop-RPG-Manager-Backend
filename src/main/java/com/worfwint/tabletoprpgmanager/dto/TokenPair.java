package com.worfwint.tabletoprpgmanager.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Encapsulates an access token and its companion refresh token returned to the client.
 */
@AllArgsConstructor
@Getter
public class TokenPair {

    /**
     * JWT access token used for authorizing API calls.
     */
    private final String accessToken;

    /**
     * JWT refresh token used to obtain new access tokens.
     */
    private final String refreshToken;
}
