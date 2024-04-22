package com.example.holdforeground.daemon

import android.content.Context
import android.content.Intent

open class DaemonOnBootReceiver(
    private val daemonControl: DaemonService.DaemonControlCompanion = DaemonService,
) : OnBootReceiver() {

    final override fun onBoot(context: Context, intent: Intent) {
        daemonControl.start(context)
    }
}
