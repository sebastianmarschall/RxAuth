# RxAuth
Reactive Auth APIs Wrapper Library for Google's [Smart Lock for Passwords API][1].

[![API](https://img.shields.io/badge/API-14%2B-blue.svg?style=flat)](https://android-arsenal.com/api?level=14)
[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-RxAuth-orange.svg?style=true)](https://android-arsenal.com/details/1/6342)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.sebastianmarschall/rxauth/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.sebastianmarschall/rxauth)

## Usage

### Build RxAuth object

```java
val rxAuth = RxAuth.Builder(context).setPasswordLoginSupported(true).setAccountTypes(IdentityProviders.GOOGLE).build()
```

### Retrieve Credentials
```java
rxAuth.retrieveCredentials().subscribe({
          // use Credential to sign in into your app
        }, {
            if (it is StatusException) {
                if (it.status.hasResolution()) {
                    try {
                        it.status.startResolutionForResult(activity, CREDENTIAL_REQUEST_RC)
                    } catch (e1: IntentSender.SendIntentException) {
                        Log.e(TAG, "RxAuth: Failed to send resolution.")
                    }

                } else {
                     // The user must create an account or sign in manually.
                     Log.e(TAG, "RxAuth: Unsuccessful credential request.");
                }
            }
        })
```

Watch for a StatusException in the onError() callback. When the user has stored more than one Credential `Status.hasResolution()` returns `true`. In this case call `startResolutionForResult()` to prompt the user to choose an account. You will get the chosen credentials in the activity's `onActivityResult()` method by calling the `retrieveCredentialFromIntent(Intent)` method on the `RxAuth` object that you created.

```java
override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
   super.onActivityResult(requestCode, resultCode, data)
   if (requestCode == CREDENTIAL_REQUEST_RC) {
       if (resultCode == RESULT_OK) {
           rxAuth.retrieveCredentialFromIntent(data).subscribe({ credentials ->
               // use Credential to sign in into your app
           })
       }
   }
}
```

### Store Credentials
```java
rxAuth.storeCredentials().subscribe( .... )
```

In case the credentials are new the user must confirm the store request. Again watch for a StatusException in the onError() callback. Resolve the save request with `startResolutionForResult()` to prompt the user for confirmation.

```java
override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
   super.onActivityResult(requestCode, resultCode, data)
   if (requestCode == CREDENTIAL_STORE_RC) {
       if (resultCode == RESULT_OK) {
           Log.d(TAG, "RxAuth: user stored credentials")
       } else {
           Log.d(TAG, "RxAuth: user canceled storing credentials")
       }
   }
}
```

### Delete Credentials
```java
rxAuth.deleteCredential().subscribe( .... )
```

## Add RxAuth To Your Project

Add this to your **build.gradle** file:
```java
dependencies {
     compile 'com.sebastianmarschall:rxauth:0.3.1'
}
```

## Additional Notes

This library is strongly inspired by the [RxSmartLock][2] but is written in Kotlin and uses RxJava2.

## License

```
Copyright 2017 Sebastian Marschall

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

 [1]: https://developers.google.com/identity/smartlock-passwords/android/
 [2]: https://github.com/ShlMlkzdh/RxSmartLock
