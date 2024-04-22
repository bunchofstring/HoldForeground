package com.example.holdforeground

import android.os.Bundle
import androidx.activity.ComponentActivity

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        HoldForegroundService.start(applicationContext)
        finish()
    }
}
