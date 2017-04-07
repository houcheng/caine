package com.caine.exception;

/**
 * An exception indicates target window is not found when JVM calls X11 library
 * to activate window.
 */
public class WindowNotFoundException extends RuntimeException {
    public WindowNotFoundException(String errMsg) {
        super(errMsg);
    }
}
