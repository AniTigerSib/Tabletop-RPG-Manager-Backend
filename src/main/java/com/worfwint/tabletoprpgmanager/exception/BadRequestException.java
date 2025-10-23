package com.worfwint.tabletoprpgmanager.exception;

public class BadRequestException extends TabletopRpgManagerException {

    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
