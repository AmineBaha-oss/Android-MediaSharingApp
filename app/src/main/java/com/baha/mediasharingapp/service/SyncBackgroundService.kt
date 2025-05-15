package com.baha.mediasharingapp.service

import android.app.Service
import android.content.Intent
import android.os.IBinder

/** Optional background sync skeleton */
class SyncBackgroundService : Service() {
    override fun onCreate() { /* schedule sync work… */ }
    override fun onBind(intent: Intent?) = null
}
