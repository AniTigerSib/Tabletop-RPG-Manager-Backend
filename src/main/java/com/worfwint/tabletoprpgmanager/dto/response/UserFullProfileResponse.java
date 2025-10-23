package com.worfwint.tabletoprpgmanager.dto.response;

import java.time.LocalDateTime;
import java.util.Set;

import com.worfwint.tabletoprpgmanager.entity.enums.UserRole;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for full user profile response.
 * @author michael
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserFullProfileResponse {
    private Long id;
    private String username;
    private String email;
    private String displayName;
    private String bio;
    private String avatarUrl;
    private Set<UserRole> roles;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
