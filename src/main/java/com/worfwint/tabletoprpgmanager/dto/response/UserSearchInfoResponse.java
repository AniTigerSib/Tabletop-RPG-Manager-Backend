package com.worfwint.tabletoprpgmanager.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for info about user in search response.
 * @author michael
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSearchInfoResponse {
    private Long id;
    private String username;
    private String displayName;
    private String avatarUrl;
}
