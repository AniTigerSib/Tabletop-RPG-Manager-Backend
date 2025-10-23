package com.worfwint.tabletoprpgmanager.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 *
 * @author michael
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class RefreshRequest {
    @NotBlank private String refreshToken;
}
