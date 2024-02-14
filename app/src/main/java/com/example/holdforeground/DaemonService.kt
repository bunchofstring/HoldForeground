package com.example.holdforeground

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat

class DaemonService: Service() {

    companion object {
        private val CHANNEL_ID = DaemonService::class.simpleName!!
        private const val CHANNEL_NAME = "C"
        private const val CHANNEL_DESCRIPTION = "D"
        private const val ACTION_SHOW = "ACTION_SHOW"
        private const val ACTION_DISMISS = "ACTION_DISMISS"
        fun start(context: Context) {
            Intent(context, DaemonService::class.java)
                .let(context::startForegroundService)
        }
    }

    private val overlay: ScreenLifecycleControl by lazy { Overlay(applicationContext, true) }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        when (intent?.action) {
            ACTION_SHOW -> overlay.show()
            ACTION_DISMISS -> overlay.dismiss()
            else -> overlay.show()
        }
        return START_STICKY
    }

    override fun onBind(p0: Intent?) = null

    private fun createNotificationChannel() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = CHANNEL_DESCRIPTION
        }
        notificationManager.createNotificationChannel(channel)
    }

    private fun startForeground() = ServiceCompat.startForeground(
        this,
        Int.MAX_VALUE,
        getNotification(),
        getServiceType(),
    )

    private fun getNotification() = NotificationCompat.Builder(this, CHANNEL_ID)
        .setSmallIcon(android.R.drawable.ic_lock_lock)
        .setContentTitle("A")
        .setContentText("B")
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .build()

    private fun getServiceType() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
        ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
    } else {
        ServiceInfo.FOREGROUND_SERVICE_TYPE_NONE
    }
}

interface ScreenLifecycleControl {
    fun show()
    fun dismiss()
}
