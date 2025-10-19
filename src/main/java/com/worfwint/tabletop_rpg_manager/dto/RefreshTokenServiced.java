package com.worfwint.tabletop_rpg_manager.dto;

import java.util.Date;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
/**
 *
 * @author michael
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RefreshTokenServiced {
    private UUID jti;
    private String token;
    private Date expiresAt;
}
