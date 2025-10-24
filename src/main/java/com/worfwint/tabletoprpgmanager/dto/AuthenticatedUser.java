package com.worfwint.tabletoprpgmanager.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Lightweight representation of an authenticated user injected into controller methods.
 */
@AllArgsConstructor
@Getter
public class AuthenticatedUser {

    /**
     * Identifier of the authenticated user.
     */
    private final Long id;

    /**
     * Username associated with the authenticated user.
     */
    private final String username;

    /**
     * Email address associated with the authenticated user.
     */
    private final String email;
}
