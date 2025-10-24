package com.worfwint.tabletoprpgmanager.exception;

/**
 * Base type for all custom application exceptions.
 */
public class TabletopRpgManagerException extends RuntimeException {

    /**
     * Creates a new exception with a detailed message.
     *
     * @param message explanation of the failure
     */
    public TabletopRpgManagerException(String message) {
        super(message);
    }

    /**
     * Creates a new exception with a message and underlying cause.
     *
     * @param message explanation of the failure
     * @param cause original reason for the failure
     */
    public TabletopRpgManagerException(String message, Throwable cause) {
        super(message, cause);
    }
}
