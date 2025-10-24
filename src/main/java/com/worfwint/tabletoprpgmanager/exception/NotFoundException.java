package com.worfwint.tabletoprpgmanager.exception;

/**
 * Indicates that a requested resource could not be found.
 */
public class NotFoundException extends TabletopRpgManagerException {

    /**
     * Creates the exception with a descriptive message.
     *
     * @param message explanation of what could not be found
     */
    public NotFoundException(String message) {
        super(message);
    }

    /**
     * Creates the exception with a message and underlying cause.
     *
     * @param message explanation of what could not be found
     * @param cause original cause of the missing resource
     */
    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
