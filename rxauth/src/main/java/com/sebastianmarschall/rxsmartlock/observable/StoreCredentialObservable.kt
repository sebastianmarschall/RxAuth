package com.sebastianmarschall.rxsmartlock.observable

import android.content.Context
import android.os.Bundle
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.credentials.Credential
import com.google.android.gms.common.api.Result
import com.sebastianmarschall.rxsmartlock.exception.StatusException
import io.reactivex.SingleEmitter

class StoreCredentialObservable(context: Context, val credential: Credential) : BaseCredentialObservable<Boolean>(context) {

    override fun getGoogleApiClientCallback(subscriber: SingleEmitter<in Boolean>): BaseGoogleApiClientCallback<Boolean> {
        return object : BaseGoogleApiClientCallback<Boolean>(subscriber) {

            override fun onConnected(p0: Bundle?) {
                try {
                    Auth.CredentialsApi.save(googleApiClient, credential).setResultCallback(this)
                } catch (e: Exception) {
                    subscriber.onError(e)
                }
            }

            override fun onResult(result: Result) {
                val status = result.status
                if (status.isSuccess) {
                    subscriber.onSuccess(true)
                } else {
                    subscriber.onError(StatusException(status))
                }
            }
        }
    }
}