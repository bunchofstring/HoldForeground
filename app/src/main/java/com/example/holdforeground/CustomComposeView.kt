package com.example.holdforeground

import android.content.Context
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView

object CustomComposeView : Overlay.CustomComposeViewFactory {
    override fun from(context: Context) = ComposeView(context).apply {
        setContent {
            var minimized by remember { mutableStateOf(false) }
            when (minimized) {
                true -> CustomContentMinimized(onMaximize = { minimized = false })
                false -> CustomContent(onMinimize = { minimized = true })
            }
            windowInsetsController?.let {
                setSystemBarsVisible(this, it, minimized)
            }
        }
    }
    private fun setSystemBarsVisible(composeView: ComposeView, windowInsetsController: WindowInsetsController, visible: Boolean) {
        composeView.setOnApplyWindowInsetsListener { view, windowInsets ->
            windowInsetsController.run {
                when (visible) {
                    true -> {
                        systemBarsBehavior = WindowInsetsController.BEHAVIOR_DEFAULT
                        show(WindowInsets.Type.systemBars())
                    }

                    false -> {
                        systemBarsBehavior =
                            WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                        hide(WindowInsets.Type.systemBars())
                    }
                }
            }
            return@setOnApplyWindowInsetsListener view.onApplyWindowInsets(windowInsets)
        }
    }
}
