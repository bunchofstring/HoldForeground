package com.example.holdforeground

import com.example.holdforeground.daemon.DaemonOnBootReceiver

class HoldForegroundOnBootReceiver : DaemonOnBootReceiver(HoldForegroundService)
