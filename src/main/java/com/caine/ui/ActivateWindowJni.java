package com.caine.ui;

import com.caine.exception.WindowNotFoundException;

/**
 * Wrapper object for invoking activateWindow() JNI call.
 */
public class ActivateWindowJni {

    static {
        System.loadLibrary("awjni");
    }


    public void callActivateWindow(String windowName) throws WindowNotFoundException {
        int errorCode = activateWindow(windowName.getBytes());
        if (errorCode != 0) {
            String errMsg = String.format("Window %s not found exception", windowName);
            throw new WindowNotFoundException(errMsg);
        }
    }

    private native int activateWindow(byte[] windowName);
}
