package com.worfwint.tabletoprpgmanager.common.dto;

/**
 * Encapsulates an access token and its companion refresh token returned to the client.
 *
 * @param accessToken  JWT access token used for authorizing API calls.
 * @param refreshToken JWT refresh token used to obtain new access tokens.
 */
public record TokenPair(String accessToken, String refreshToken) {}