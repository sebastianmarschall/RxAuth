package com.sebastianmarschall.rxsmartlock.exception;

import com.google.android.gms.common.api.Status;

public class StatusException extends RuntimeException {

    private Status status;

    public StatusException(Status status) {
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }

}
