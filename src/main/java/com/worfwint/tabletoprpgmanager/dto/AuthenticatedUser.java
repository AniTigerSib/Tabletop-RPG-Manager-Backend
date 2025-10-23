package com.worfwint.tabletoprpgmanager.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 *
 * @author michael
 */
@AllArgsConstructor
@Getter
public class AuthenticatedUser {
    private final Long id;
    private final String username;
    private final String email;
}
