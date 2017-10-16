package com.sebastianmarschall.rxsmartlock.observable


import android.content.Context
import android.os.Bundle
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.credentials.Credential
import com.google.android.gms.auth.api.credentials.CredentialRequest
import com.google.android.gms.auth.api.credentials.CredentialRequestResult
import com.google.android.gms.common.api.Result
import com.sebastianmarschall.rxsmartlock.exception.StatusException
import io.reactivex.SingleEmitter

class RetrieveCredentialObservable(context: Context, val credentialRequest: CredentialRequest) : BaseCredentialObservable<Credential>(context) {

    override fun getGoogleApiClientCallback(subscriber: SingleEmitter<in Credential>): BaseGoogleApiClientCallback<Credential> {
        return object : BaseGoogleApiClientCallback<Credential>(subscriber) {

            override fun onConnected(p0: Bundle?) {
                try {
                    Auth.CredentialsApi.request(googleApiClient, credentialRequest).setResultCallback(this)
                } catch (e: Exception) {
                    subscriber.onError(e)
                }
            }

            override fun onResult(result: Result) {
                (result as? CredentialRequestResult)?.apply {
                    val status = this.status
                    if (status.isSuccess) {
                        subscriber.onSuccess(this.credential)
                    } else {
                        subscriber.onError(StatusException(status))
                    }
                }
            }
        }
    }
}