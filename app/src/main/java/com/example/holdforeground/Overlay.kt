package com.example.holdforeground

import android.content.Context
import android.content.Context.WINDOW_SERVICE
import android.graphics.PixelFormat
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.compose.runtime.Recomposer
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

class Overlay(context: Context, fullScreen: Boolean = true): ScreenLifecycleControl {

    private val windowManager = context.getSystemService(WINDOW_SERVICE) as WindowManager
    private val composeView = ComposeView(context).apply {
        if(fullScreen) {
            setOnApplyWindowInsetsListener { view, windowInsets ->
                windowInsetsController?.run {
                    systemBarsBehavior =
                        WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                    hide(WindowInsets.Type.systemBars())
                }
                view.onApplyWindowInsets(windowInsets)
            }
        }else{
            fitsSystemWindows = true
        }
        setContent {
            CustomContent()
        }
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
            setViewTreeViewModelStoreOwner(composeView.findViewTreeViewModelStoreOwner())
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
        WindowManager.LayoutParams.MATCH_PARENT,
        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
        PixelFormat.TRANSLUCENT
    )
}
