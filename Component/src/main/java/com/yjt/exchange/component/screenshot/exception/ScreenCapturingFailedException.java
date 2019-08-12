package com.hynet.heebit.components.screenshot.exception;

public class ScreenCapturingFailedException extends RuntimeException {

    public ScreenCapturingFailedException() {
    }

    public ScreenCapturingFailedException(String message) {
        super(message);
    }

    public ScreenCapturingFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public ScreenCapturingFailedException(Throwable cause) {
        super(cause);
    }

}
