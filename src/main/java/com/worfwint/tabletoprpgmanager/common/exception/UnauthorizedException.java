package com.worfwint.tabletoprpgmanager.common.exception;

/**
 * Raised when a user attempts to access a resource without sufficient privileges.
 */
public class UnauthorizedException extends TabletopRpgManagerException {

    /**
     * Creates the exception with a descriptive message.
     *
     * @param message explanation of the unauthorized access
     */
    public UnauthorizedException(String message) {
        super(message);
    }

    /**
     * Creates the exception with a message and underlying cause.
     *
     * @param message explanation of the unauthorized access
     * @param cause original cause of the security failure
     */
    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }
}
