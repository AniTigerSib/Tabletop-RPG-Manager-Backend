package com.worfwint.tabletoprpgmanager.exception;

/**
 * Thrown when a user lookup fails to find a matching record.
 */
public class UserNotFoundException extends NotFoundException {

    /**
     * Creates the exception with a default message.
     */
    public UserNotFoundException() {
        super("User not found");
    }

    /**
     * Creates the exception with a custom message.
     *
     * @param message description of the lookup failure
     */
    public UserNotFoundException(String message) {
        super(message);
    }
}
