package com.worfwint.tabletop_rpg_manager.dto.response;

import com.worfwint.tabletop_rpg_manager.entity.enums.UserRole;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for public user profile response.
 * @author michael
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPublicProfileResponse {
    private Long id;
    private String username;
    // private String email;
    private String displayName;
    private String bio;
    private String avatarUrl;
    private UserRole role;
    // private LocalDateTime createdAt;
    // private LocalDateTime updatedAt;
}
