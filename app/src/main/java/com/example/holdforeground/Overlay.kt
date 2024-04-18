package com.example.holdforeground

import android.content.Context
import android.content.Context.WINDOW_SERVICE
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.compose.runtime.Recomposer
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.AndroidUiDispatcher
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.compositionContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class Overlay(context: Context) : ScreenLifecycleControl {

    private val windowManager = context.getSystemService(WINDOW_SERVICE) as WindowManager
    private val composeView = ComposeView(context).apply {
        fitsSystemWindows = false
        setContent {
            var minimized by remember { mutableStateOf(false) }
            when (minimized) {
                true -> CustomContentMinimized(onMaximize = { minimized = false })
                false -> CustomContent(onMinimize = { minimized = true })
            }

            windowInsetsController?.let {
                setInsetsVisible(this, it, !minimized)
            }
        }
        fitsSystemWindows = false
    }

    init {
        val lifecycleOwner = MyLifecycleOwner()
        val coroutineContext = AndroidUiDispatcher.CurrentThread
        val recomposer = Recomposer(coroutineContext)

        lifecycleOwner.run {
            performRestore(null)
            handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        }

        composeView.run {
            setViewTreeSavedStateRegistryOwner(lifecycleOwner)
            setViewTreeLifecycleOwner(lifecycleOwner)
            setViewTreeViewModelStoreOwner(findViewTreeViewModelStoreOwner())
            compositionContext = recomposer
        }

        CoroutineScope(coroutineContext).launch {
            recomposer.runRecomposeAndApplyChanges()
        }
    }

    override fun show() = windowManager.addView(composeView, getLayoutParams())

    override fun dismiss() = windowManager.removeView(composeView)

    private fun getLayoutParams() = WindowManager.LayoutParams(
        WindowManager.LayoutParams.MATCH_PARENT,
        WindowManager.LayoutParams.WRAP_CONTENT,
        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
        PixelFormat.TRANSLUCENT,
    ).apply {
        gravity = Gravity.TOP
    }

    private fun setInsetsVisible(composeView: ComposeView, windowInsetsController: WindowInsetsController, visible: Boolean) {
        composeView.setOnApplyWindowInsetsListener { view, windowInsets ->
            windowInsetsController.run {
                when (visible) {
                    true -> {
                        systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                        hide(WindowInsets.Type.systemBars())
                    }
                    false -> {
                        systemBarsBehavior = WindowInsetsController.BEHAVIOR_DEFAULT
                        show(WindowInsets.Type.systemBars())
                    }
                }
            }
            return@setOnApplyWindowInsetsListener view.onApplyWindowInsets(windowInsets)
        }
    }
}
