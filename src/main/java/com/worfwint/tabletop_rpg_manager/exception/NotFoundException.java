package com.worfwint.tabletop_rpg_manager.exception;

public class NotFoundException extends TabletopRpgManagerException {

    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
