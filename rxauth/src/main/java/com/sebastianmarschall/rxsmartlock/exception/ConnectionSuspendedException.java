package com.sebastianmarschall.rxsmartlock.exception;

public class ConnectionSuspendedException extends RuntimeException {

    private int code;

    public ConnectionSuspendedException(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

}
