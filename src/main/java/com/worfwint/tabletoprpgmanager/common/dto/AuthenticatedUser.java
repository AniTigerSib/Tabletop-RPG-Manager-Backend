package com.worfwint.tabletoprpgmanager.common.dto;

/**
 * Lightweight representation of an authenticated user injected into controller methods.
 *
 * @param id       Identifier of the authenticated user.
 * @param username Username associated with the authenticated user.
 * @param email    Email address associated with the authenticated user.
 */
public record AuthenticatedUser(Long id, String username, String email) {}