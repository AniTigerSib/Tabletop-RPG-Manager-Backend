package com.worfwint.tabletoprpgmanager.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 *
 * @author michael
 */
@AllArgsConstructor
@Getter
public class TokenPair {
    private String accessToken;
    private String refreshToken;
}
