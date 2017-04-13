package com.caine.exception;

import org.junit.Test;

public class WindowNotFoundExceptionTest {
    @Test(expected = WindowNotFoundException.class)
    public void testWindowNotFoundException() {
        throw new WindowNotFoundException("");
    }
}