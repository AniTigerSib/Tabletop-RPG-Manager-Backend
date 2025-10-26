package com.worfwint.tabletoprpgmanager.exception;

/**
 * Exception thrown when a requested news comment cannot be located.
 */
public class NewsCommentNotFoundException extends NotFoundException {

    /**
     * Creates the exception with a default message.
     */
    public NewsCommentNotFoundException() {
        super("News comment not found");
    }

    /**
     * Creates the exception with a custom message.
     *
     * @param message description of the missing comment
     */
    public NewsCommentNotFoundException(String message) {
        super(message);
    }
}
