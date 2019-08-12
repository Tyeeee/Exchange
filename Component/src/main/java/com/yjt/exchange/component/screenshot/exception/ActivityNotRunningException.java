package com.hynet.heebit.components.screenshot.exception;

public class ActivityNotRunningException extends RuntimeException {

    public ActivityNotRunningException() {
    }

    public ActivityNotRunningException(String message) {
        super(message);
    }

    public ActivityNotRunningException(String message, Throwable cause) {
        super(message, cause);
    }

    public ActivityNotRunningException(Throwable cause) {
        super(cause);
    }
}
