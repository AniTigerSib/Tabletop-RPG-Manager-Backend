package com.worfwint.tabletop_rpg_manager.exception;

public class UnauthorizedException extends TabletopRpgManagerException {

    public UnauthorizedException(String message) {
        super(message);
    }

    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }
}
