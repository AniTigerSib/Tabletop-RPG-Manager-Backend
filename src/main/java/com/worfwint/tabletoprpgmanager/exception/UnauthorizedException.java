package com.worfwint.tabletoprpgmanager.exception;

public class UnauthorizedException extends TabletopRpgManagerException {

    public UnauthorizedException(String message) {
        super(message);
    }

    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }
}
