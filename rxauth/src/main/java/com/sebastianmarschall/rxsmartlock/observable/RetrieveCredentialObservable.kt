package com.sebastianmarschall.rxsmartlock.observable


import android.content.Context
import android.os.Bundle
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.credentials.Credential
import com.google.android.gms.auth.api.credentials.CredentialRequest
import com.google.android.gms.auth.api.credentials.CredentialRequestResult
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.common.api.Status
import com.sebastianmarschall.rxsmartlock.exception.ConnectionException
import com.sebastianmarschall.rxsmartlock.exception.ConnectionSuspendedException
import com.sebastianmarschall.rxsmartlock.exception.StatusException
import io.reactivex.SingleEmitter
import io.reactivex.SingleOnSubscribe

class RetrieveCredentialObservable(private val mContext: Context, private val mCredentialRequest: CredentialRequest) : SingleOnSubscribe<Credential> {

    override fun subscribe(subscriber: SingleEmitter<Credential>) {

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

    private fun buildGoogleApiClient(observer: SingleEmitter<in Credential>): GoogleApiClient {

        val clientCallbacks = GoogleApiClientCallbacks(observer)
        val client = GoogleApiClient.Builder(mContext)
                .addApi(Auth.CREDENTIALS_API)
                .addConnectionCallbacks(clientCallbacks)
                .addOnConnectionFailedListener(clientCallbacks)
                .build()
        clientCallbacks.setGoogleApiClient(client)
        return client

    }

    private inner class GoogleApiClientCallbacks(private val subscriber: SingleEmitter<in Credential>) : GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback<CredentialRequestResult> {

        private var googleApiClient: GoogleApiClient? = null

        override fun onConnected(bundle: Bundle?) {

            try {
                Auth.CredentialsApi.request(googleApiClient, mCredentialRequest)
                        .setResultCallback(this)
            } catch (e: Exception) {
                subscriber.onError(e)
            }

        }

        override fun onConnectionSuspended(i: Int) {
            subscriber.onError(ConnectionSuspendedException(i))
        }

        override fun onConnectionFailed(connectionResult: ConnectionResult) {
            subscriber.onError(ConnectionException(connectionResult))
        }

        override fun onResult(credentialRequestResult: CredentialRequestResult) {

            val status = credentialRequestResult.status
            if (status.isSuccess) {
                onCredentialRetrieved(credentialRequestResult.credential)
            } else {
                resolveResult(status)
            }

        }

        private fun onCredentialRetrieved(credential: Credential) {
            subscriber.onSuccess(credential)
        }

        private fun resolveResult(status: Status) {
            subscriber.onError(StatusException(status))
        }

        fun setGoogleApiClient(googleApiClient: GoogleApiClient) {
            this.googleApiClient = googleApiClient
        }

    }

}
