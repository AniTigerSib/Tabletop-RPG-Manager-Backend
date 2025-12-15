package com.worfwint.tabletoprpgmanager.auth.exception;

import com.worfwint.tabletoprpgmanager.common.exception.BadRequestException;

/**
 * Indicates that a registration request attempted to use an email address that is already taken.
 */
public class EmailAlreadyExistsException extends BadRequestException {

    /**
     * Creates the exception with a default message indicating the email conflict.
     */
    public EmailAlreadyExistsException() {
        super("Email already exists");
    }

    /**
     * Creates the exception with a custom message describing the email conflict.
     *
     * @param message custom description of the error
     */
    public EmailAlreadyExistsException(String message) {
        super(message);
    }
}
