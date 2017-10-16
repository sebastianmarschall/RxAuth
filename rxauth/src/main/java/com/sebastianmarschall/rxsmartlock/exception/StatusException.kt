package com.sebastianmarschall.rxsmartlock.exception

import com.google.android.gms.common.api.Status

class StatusException(val status: Status) : RuntimeException()
