package com.sebastianmarschall.rxsmartlock.observable;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.CredentialRequest;
import com.google.android.gms.auth.api.credentials.CredentialRequestResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.sebastianmarschall.rxsmartlock.exception.ConnectionException;
import com.sebastianmarschall.rxsmartlock.exception.ConnectionSuspendedException;
import com.sebastianmarschall.rxsmartlock.exception.StatusException;

import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Cancellable;

public class RetrieveCredentialObservable implements ObservableOnSubscribe<Credential> {

    private Context mContext;
    private CredentialRequest mCredentialRequest;

    public RetrieveCredentialObservable(Context context, CredentialRequest request) {
        mContext = context;
        mCredentialRequest = request;
    }

    @Override
    public void subscribe(ObservableEmitter<Credential> subscriber) throws Exception {

        final GoogleApiClient googleApiClient = buildGoogleApiClient(subscriber);

        try {
            googleApiClient.connect();
        } catch (Exception e) {
            subscriber.onError(e);
        }

        subscriber.setCancellable(new Cancellable() {
            @Override
            public void cancel() throws Exception {
                if (googleApiClient.isConnected() || googleApiClient.isConnecting()) {
                    googleApiClient.disconnect();
                }
            }
        });
    }


    private GoogleApiClient buildGoogleApiClient(ObservableEmitter<? super Credential> observer) {

        GoogleApiClientCallbacks clientCallbacks = new GoogleApiClientCallbacks(observer);
        GoogleApiClient client = new GoogleApiClient.Builder(mContext)
                .addApi(Auth.CREDENTIALS_API)
                .addConnectionCallbacks(clientCallbacks)
                .addOnConnectionFailedListener(clientCallbacks)
                .build();
        clientCallbacks.setGoogleApiClient(client);
        return client;

    }

    private class GoogleApiClientCallbacks implements GoogleApiClient.ConnectionCallbacks,
            GoogleApiClient.OnConnectionFailedListener, ResultCallback<CredentialRequestResult> {

        private GoogleApiClient googleApiClient;
        private ObservableEmitter<? super Credential> subscriber;

        public GoogleApiClientCallbacks(ObservableEmitter<? super Credential> observer) {
            subscriber = observer;
        }

        @Override
        public void onConnected(Bundle bundle) {

            try {
                Auth.CredentialsApi.request(googleApiClient, mCredentialRequest)
                        .setResultCallback(this);
            } catch (Exception e) {
                subscriber.onError(e);
            }

        }

        @Override
        public void onConnectionSuspended(int i) {
            subscriber.onError(new ConnectionSuspendedException(i));
        }

        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            subscriber.onError(new ConnectionException(connectionResult));
        }

        @Override
        public void onResult(@NonNull CredentialRequestResult credentialRequestResult) {

            Status status = credentialRequestResult.getStatus();
            if (status.isSuccess()) {
                onCredentialRetrieved(credentialRequestResult.getCredential());
            } else {
                resolveResult(status);
            }

        }

        private void onCredentialRetrieved(Credential credential) {
            subscriber.onNext(credential);
            subscriber.onComplete();
        }

        private void resolveResult(Status status) {
            subscriber.onError(new StatusException(status));
        }

        public void setGoogleApiClient(GoogleApiClient googleApiClient) {
            this.googleApiClient = googleApiClient;
        }

    }

}
