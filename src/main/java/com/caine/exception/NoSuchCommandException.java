package com.caine.exception;

/**
 * Threw by {@link com.caine.core.CommandEngine} while receiving an unrecognized command.
 */
public class NoSuchCommandException  extends RuntimeException {

    public NoSuchCommandException(String msg) {
        super(msg);
    }
}
