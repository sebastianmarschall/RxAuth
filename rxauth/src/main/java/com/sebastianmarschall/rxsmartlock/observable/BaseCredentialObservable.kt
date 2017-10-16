package com.sebastianmarschall.rxsmartlock.observable

import android.content.Context
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.common.api.GoogleApiClient
import io.reactivex.SingleEmitter
import io.reactivex.SingleOnSubscribe

abstract class BaseCredentialObservable<T>(private val context: Context) : SingleOnSubscribe<T> {

    override fun subscribe(subscriber: SingleEmitter<T>) {
        val googleApiClient = buildGoogleApiClient(subscriber)

        try {
            googleApiClient.connect()
        } catch (e: Exception) {
            subscriber.onError(e)
        }

        subscriber.setCancellable {
            if (googleApiClient.isConnected || googleApiClient.isConnecting) {
                googleApiClient.disconnect()
            }
        }
    }

    private fun buildGoogleApiClient(observer: SingleEmitter<in T>): GoogleApiClient {
        val clientCallbacks = getGoogleApiClientCallback(observer)
        val client = GoogleApiClient.Builder(context)
                .addApi(Auth.CREDENTIALS_API)
                .addConnectionCallbacks(clientCallbacks)
                .addOnConnectionFailedListener(clientCallbacks)
                .build()
        clientCallbacks.googleApiClient = client
        return client
    }

    abstract fun getGoogleApiClientCallback(subscriber: SingleEmitter<in T>): BaseGoogleApiClientCallback<T>

}