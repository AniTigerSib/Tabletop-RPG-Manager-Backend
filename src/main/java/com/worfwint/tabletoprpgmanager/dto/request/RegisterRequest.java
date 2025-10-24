package com.worfwint.tabletoprpgmanager.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request payload used when registering a new user account.
 */
@NoArgsConstructor
@Getter
@Setter
public class RegisterRequest {

    /**
     * Desired unique username for the new account.
     */
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    @Pattern(regexp = "^\\w+$", message = "Username can only contain letters, numbers and underscores")
    @NotBlank(message = "Username cannot be empty")
    private String username;

    /**
     * Email address that will be associated with the account.
     */
    @NotBlank(message = "Email is required")
    @Pattern(
        regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
        message = "Email must be valid"
    )
    private String email;

    /**
     * Plain text password supplied by the user during registration.
     */
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,64}$",
             message = "Password must contain at least one uppercase letter, one lowercase letter, one number and one special character")
    private String password;

    /**
     * Optional display name presented to other users.
     */
    private String displayName;

    /**
     * Creates a fully populated request used primarily in tests.
     *
     * @param username desired username
     * @param email email address
     * @param password plain text password
     * @param displayName optional display name
     */
    public RegisterRequest(String username, String email, String password, String displayName) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.displayName = displayName;
    }
}
