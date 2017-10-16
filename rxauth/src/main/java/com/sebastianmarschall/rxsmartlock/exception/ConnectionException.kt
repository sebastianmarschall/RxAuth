package com.sebastianmarschall.rxsmartlock.exception

import com.google.android.gms.common.ConnectionResult

class ConnectionException(val connectionResult: ConnectionResult) : RuntimeException()
