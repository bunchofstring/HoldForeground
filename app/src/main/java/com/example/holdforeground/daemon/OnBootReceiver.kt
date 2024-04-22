package com.example.holdforeground.daemon

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

abstract class OnBootReceiver : BroadcastReceiver() {
    final override fun onReceive(p0: Context?, p1: Intent?) {
        if (p0 == null || p1 == null || p1.action != Intent.ACTION_BOOT_COMPLETED) return
        onBoot(p0, p1)
    }

    abstract fun onBoot(context: Context, intent: Intent)
}
