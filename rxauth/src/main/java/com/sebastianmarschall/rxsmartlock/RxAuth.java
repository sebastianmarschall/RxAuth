package com.sebastianmarschall.rxsmartlock;

import android.content.Context;
import android.content.Intent;

import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.CredentialPickerConfig;
import com.google.android.gms.auth.api.credentials.CredentialRequest;
import com.sebastianmarschall.rxsmartlock.observable.RetrieveCredentialObservable;

import io.reactivex.Observable;
import io.reactivex.Single;

public class RxAuth {

    private Context context;
    private CredentialRequest credentialRequest;


    private RxAuth(Builder builder) {
        context = builder.getContext();
        credentialRequest = builder.getCredentialRequestBuilder().build();
    }

    public Single<Credential> retrieveCredentials() {
        return Single.create(new RetrieveCredentialObservable(context, credentialRequest));
    }

    public Single<Credential> retrieveCredentialFromIntent(Intent data) {
        return Single.just((Credential) data.getParcelableExtra(Credential.EXTRA_KEY));
    }

    public static class Builder {

        private Context mContext;
        private CredentialRequest.Builder mCredentialRequestBuilder;

        public Builder(Context context) {
            mContext = context;
            mCredentialRequestBuilder = new CredentialRequest.Builder();
        }

        public Builder setAccountTypes(String... accountTypes) {
            mCredentialRequestBuilder.setAccountTypes(accountTypes);
            return this;
        }

        public Builder setCredentialHintPickerConfig(CredentialPickerConfig config) {
            mCredentialRequestBuilder.setCredentialHintPickerConfig(config);
            return this;
        }

        public Builder setCredentialPickerConfig(CredentialPickerConfig config) {
            mCredentialRequestBuilder.setCredentialPickerConfig(config);
            return this;
        }

        public Builder setPasswordLoginSupported(boolean passwordLoginSupported) {
            mCredentialRequestBuilder.setPasswordLoginSupported(passwordLoginSupported);
            return this;
        }

        public RxAuth build() {
            return new RxAuth(this);
        }

        private Context getContext() {
            return mContext;
        }

        private CredentialRequest.Builder getCredentialRequestBuilder() {
            return mCredentialRequestBuilder;
        }

    }
}
