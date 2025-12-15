package com.worfwint.tabletoprpgmanager.common.exception;

/**
 * Thrown when an API request fails validation or contains invalid parameters.
 */
public class BadRequestException extends TabletopRpgManagerException {

    /**
     * Creates a new {@link BadRequestException} with the provided message.
     *
     * @param message details about the invalid request
     */
    public BadRequestException(String message) {
        super(message);
    }

    /**
     * Creates a new {@link BadRequestException} with a message and root cause.
     *
     * @param message details about the invalid request
     * @param cause underlying reason for the failure
     */
    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
