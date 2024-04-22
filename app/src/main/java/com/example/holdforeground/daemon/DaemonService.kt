package com.example.holdforeground.daemon

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat

/**
 * A "Daemon" brings high availability (like a system service) to userspace apps. This document
 * represents the source of truth regarding documentation. There are three components of a "Daemon"
 * 1. Service: DaemonService or a subclass (has a DaemonControlCompanion or a subclass)
 * 2. Receiver: DaemonOnBootReceiver or a subclass
 * 3. AndroidManifest declarations for Service and Receiver
 */

open class DaemonService : Service() {

    companion object : DaemonControlCompanion {
        private val CHANNEL_ID = DaemonService::class.simpleName!!
        private const val CHANNEL_NAME = "Daemon"
        private const val CHANNEL_DESCRIPTION = "Always running"
        private const val FOREGROUND_ID = Int.MAX_VALUE
    }

    override fun onCreate() {
        super.onCreate()
        initialize()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        return START_STICKY
    }

    override fun onBind(p0: Intent?) = null

    protected open fun getChannelId() = CHANNEL_ID

    protected open fun getForegroundId() = FOREGROUND_ID

    protected open fun getNotificationChannel(channelId: String) = NotificationChannel(
        channelId,
        CHANNEL_NAME,
        NotificationManager.IMPORTANCE_HIGH,
    ).apply {
        description = CHANNEL_DESCRIPTION
    }

    protected open fun getNotification(channelId: String) = NotificationCompat
        .Builder(this, channelId)
        .setCategory(NotificationCompat.CATEGORY_TRANSPORT)
        .setSmallIcon(android.R.drawable.ic_lock_lock)
        .setContentTitle("Daemon is active")
        .setContentText("Foreground service")
        .setPriority(NotificationCompat.PRIORITY_LOW)
        .build()

    private fun initialize(
        channelId: String = getChannelId(),
        foregroundId: Int = getForegroundId(),
        notificationChannel: NotificationChannel = getNotificationChannel(channelId),
        notification: Notification = getNotification(channelId),
        serviceType: Int = getServiceType(),
        notificationManager: NotificationManager = getNotificationManager(),
    ) {
        notificationManager.createNotificationChannel(notificationChannel)
        ServiceCompat.startForeground(this, foregroundId, notification, serviceType)
    }

    private fun getServiceType() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
        ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
    } else {
        ServiceInfo.FOREGROUND_SERVICE_TYPE_NONE
    }

    private fun getNotificationManager() = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    interface DaemonControlCompanion {
        fun start(context: Context) {
            context.startForegroundService(
                Intent(context, javaClass.enclosingClass),
            )
        }
    }
}
