package com.worfwint.tabletop_rpg_manager.exception;

/**
 * Base type for all custom application exceptions.
 */
public class TabletopRpgManagerException extends RuntimeException {

    public TabletopRpgManagerException(String message) {
        super(message);
    }

    public TabletopRpgManagerException(String message, Throwable cause) {
        super(message, cause);
    }
}
