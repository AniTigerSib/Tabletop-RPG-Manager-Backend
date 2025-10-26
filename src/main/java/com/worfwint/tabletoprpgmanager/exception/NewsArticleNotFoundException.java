package com.worfwint.tabletoprpgmanager.exception;

/**
 * Exception thrown when a requested news article cannot be located.
 */
public class NewsArticleNotFoundException extends NotFoundException {

    /**
     * Creates the exception with a default message.
     */
    public NewsArticleNotFoundException() {
        super("News article not found");
    }

    /**
     * Creates the exception with a custom message.
     *
     * @param message description of the missing article
     */
    public NewsArticleNotFoundException(String message) {
        super(message);
    }
}
