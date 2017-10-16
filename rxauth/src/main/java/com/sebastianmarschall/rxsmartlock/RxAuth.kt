package com.sebastianmarschall.rxsmartlock

import android.content.Context
import android.content.Intent
import android.os.Parcelable
import com.google.android.gms.auth.api.credentials.Credential
import com.google.android.gms.auth.api.credentials.CredentialPickerConfig
import com.google.android.gms.auth.api.credentials.CredentialRequest
import com.sebastianmarschall.rxsmartlock.observable.DeleteCredentialObservable
import com.sebastianmarschall.rxsmartlock.observable.RetrieveCredentialObservable
import com.sebastianmarschall.rxsmartlock.observable.StoreCredentialObservable
import io.reactivex.Single

class RxAuth private constructor(builder: Builder) {

    private val context: Context
    private val credentialRequest: CredentialRequest

    init {
        context = builder.context
        credentialRequest = builder.credentialRequestBuilder.build()
    }

    fun retrieveCredentials(): Single<Credential> {
        return Single.create(RetrieveCredentialObservable(context, credentialRequest))
    }

    fun retrieveCredentialFromIntent(data: Intent): Single<Credential> {
        return Single.just(data.getParcelableExtra<Parcelable>(Credential.EXTRA_KEY) as Credential)
    }

    fun storeCredentials(credential: Credential): Single<Boolean> {
        return Single.create(StoreCredentialObservable(context, credential))
    }

    fun deleteCredential(credential: Credential): Single<Boolean> {
        return Single.create(DeleteCredentialObservable(context, credential))
    }

    class Builder(val context: Context) {
        val credentialRequestBuilder: CredentialRequest.Builder = CredentialRequest.Builder()

        fun setAccountTypes(vararg accountTypes: String): Builder {
            credentialRequestBuilder.setAccountTypes(*accountTypes)
            return this
        }

        fun setCredentialHintPickerConfig(config: CredentialPickerConfig): Builder {
            credentialRequestBuilder.setCredentialHintPickerConfig(config)
            return this
        }

        fun setCredentialPickerConfig(config: CredentialPickerConfig): Builder {
            credentialRequestBuilder.setCredentialPickerConfig(config)
            return this
        }

        fun setPasswordLoginSupported(passwordLoginSupported: Boolean): Builder {
            credentialRequestBuilder.setPasswordLoginSupported(passwordLoginSupported)
            return this
        }

        fun build(): RxAuth {
            return RxAuth(this)
        }

    }
}
