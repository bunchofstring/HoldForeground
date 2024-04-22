package com.example.holdforeground

import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.holdforeground.daemon.DaemonService

class HoldForegroundService : DaemonService() {

    companion object : DaemonControlCompanion {
        private const val ACTION_SHOW = "ACTION_SHOW"
        private const val ACTION_DISMISS = "ACTION_DISMISS"
    }

    private val overlay: ScreenLifecycleControl by lazy { Overlay(applicationContext) }

    override fun getNotification(channelId: String) = NotificationCompat
        .Builder(this, channelId)
        .setSmallIcon(android.R.drawable.ic_lock_lock)
        .setContentTitle("Holding the foreground")
        .setContentText("Always visible with few exceptions")
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .build()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId).also {
            when (intent?.action) {
                ACTION_SHOW -> overlay.show()
                ACTION_DISMISS -> overlay.dismiss()
                else -> overlay.show()
            }
        }
    }

    interface ScreenLifecycleControl {
        fun show()
        fun dismiss()
    }
}
