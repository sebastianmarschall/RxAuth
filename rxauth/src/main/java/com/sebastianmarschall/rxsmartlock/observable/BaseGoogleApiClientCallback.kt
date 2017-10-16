package com.sebastianmarschall.rxsmartlock.observable

import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.Result
import com.google.android.gms.common.api.ResultCallback
import com.sebastianmarschall.rxsmartlock.exception.ConnectionException
import com.sebastianmarschall.rxsmartlock.exception.ConnectionSuspendedException
import io.reactivex.SingleEmitter

abstract class BaseGoogleApiClientCallback<T>(private val subscriber: SingleEmitter<in T>) : GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback<Result> {

    var googleApiClient: GoogleApiClient? = null

    override fun onConnectionSuspended(i: Int) {
        subscriber.onError(ConnectionSuspendedException(i))
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        subscriber.onError(ConnectionException(connectionResult))
    }

}