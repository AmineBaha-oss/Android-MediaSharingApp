package com.baha.mediasharingapp.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.baha.mediasharingapp.R

class ForegroundNotificationService : Service() {
    override fun onCreate() {
        super.onCreate()
        val channelId = createChannel()
        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("MediaSharingApp")
            .setContentText("Service running")
            // use your launcher icon as a placeholder
            .setSmallIcon(R.mipmap.ic_launcher)
            .build()
        startForeground(1, notification)
    }

    override fun onBind(intent: Intent?) = null

    private fun createChannel(): String {
        val channelId = "media_channel"
        val mgr = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        mgr.createNotificationChannel(
            NotificationChannel(
                channelId,
                "App Service",
                NotificationManager.IMPORTANCE_LOW
            )
        )
        return channelId
    }
}
