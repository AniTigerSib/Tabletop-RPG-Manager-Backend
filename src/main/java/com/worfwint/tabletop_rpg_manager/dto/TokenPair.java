package com.worfwint.tabletop_rpg_manager.dto;

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
