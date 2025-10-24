package com.worfwint.tabletoprpgmanager.exception;

/**
 * Signals that authentication failed due to incorrect username, email, or password.
 */
public class InvalidCredentialsException extends UnauthorizedException {

    /**
     * Creates the exception with a default message.
     */
    public InvalidCredentialsException() {
        super("Invalid credentials");
    }

    /**
     * Creates the exception with a custom message.
     *
     * @param message custom description of the authentication failure
     */
    public InvalidCredentialsException(String message) {
        super(message);
    }
}
