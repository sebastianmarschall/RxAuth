package com.sebastianmarschall.rxsmartlock

import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.google.android.gms.auth.api.credentials.Credential
import com.google.android.gms.auth.api.credentials.IdentityProviders
import com.sebastianmarschall.rxsmartlock.exception.StatusException


class MainActivity : AppCompatActivity() {

    companion object {
        private val TAG = MainActivity::class.java.simpleName
        private val CREDENTIAL_REQUEST_RC = 111
    }

    lateinit var rxAuth: RxAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        rxAuth = RxAuth.Builder(this).setAccountTypes(IdentityProviders.GOOGLE, IdentityProviders.FACEBOOK).setPasswordLoginSupported(true).build()
        rxAuth.retrieveCredentials().subscribe({
            onReceivedCredentials(it)
        }, {
            if (it is StatusException) {
                val status = it.status
                if (status.hasResolution()) {
                    try {
                        status.startResolutionForResult(this, CREDENTIAL_REQUEST_RC)
                    } catch (e1: IntentSender.SendIntentException) {
                        Log.e(TAG, "STATUS: Failed to send resolution.")
                    }

                } else {
                    // The user must create an account or sign in manually.
                    Log.e(TAG, "STATUS: Unsuccessful credential request.")
                }
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {

        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CREDENTIAL_REQUEST_RC) {
            if (resultCode == Activity.RESULT_OK) {
                rxAuth.retrieveCredentialFromIntent(data).subscribe({ credentials ->
                    onReceivedCredentials(credentials)
                })
            } else {
                Log.e(TAG, "Credential Read: NOT OK")
            }
        }
    }

    private fun onReceivedCredentials(credentials: Credential) {
        Toast.makeText(this, "You logged in as: " + credentials.name, Toast.LENGTH_SHORT).show()
    }
}
