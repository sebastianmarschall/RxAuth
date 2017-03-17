package com.sebastianmarschall.rxsmartlock.exception;

import com.google.android.gms.common.ConnectionResult;

public class ConnectionException extends RuntimeException {

    private ConnectionResult connectionResult;

    public ConnectionException(ConnectionResult result) {
        connectionResult = result;
    }

    public ConnectionResult getConnectionResult() {
        return connectionResult;
    }

}
