package com.example.holdforeground

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class OnBootReceiver : BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        if (p1?.action == Intent.ACTION_BOOT_COMPLETED) {
            p0?.let(HoldForegroundService::start)
        }
    }
}
