package com.hynet.heebit.components.screenshot.exception;

public class IllegalScreenSizeException extends Exception {

    public IllegalScreenSizeException() {
    }

    public IllegalScreenSizeException(String message) {
        super(message);
    }

    public IllegalScreenSizeException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalScreenSizeException(Throwable cause) {
        super(cause);
    }
}
