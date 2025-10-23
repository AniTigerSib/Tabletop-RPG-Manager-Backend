package com.worfwint.tabletoprpgmanager.exception;

public class NotFoundException extends TabletopRpgManagerException {

    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
