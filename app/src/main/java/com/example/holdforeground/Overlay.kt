package com.example.holdforeground

import android.content.Context
import android.content.Context.WINDOW_SERVICE
import android.graphics.PixelFormat
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Recomposer
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.AndroidUiDispatcher
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.compositionContext
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch



class Overlay(context: Context): ScreenLifecycleControl {

    private val composeView = ComposeView(context).apply {
        fitsSystemWindows = false
        setContent {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                CustomContent()
            }
        }
        setOnApplyWindowInsetsListener { view, windowInsets ->
            windowInsetsController?.run {
                systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                hide(WindowInsets.Type.systemBars())
            }
            view.onApplyWindowInsets(windowInsets)
        }
    }
    private val windowManager = context.getSystemService(WINDOW_SERVICE) as WindowManager

    override fun show() {
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

        windowManager.addView(
            composeView,
            WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                PixelFormat.TRANSLUCENT
            )
        )
    }

    override fun dismiss() {
        windowManager.removeView(composeView)
    }
}

internal class MyLifecycleOwner : SavedStateRegistryOwner {
    private var mLifecycleRegistry: LifecycleRegistry = LifecycleRegistry(this)
    private var mSavedStateRegistryController: SavedStateRegistryController = SavedStateRegistryController.create(this)
//    /**
//     * @return True if the Lifecycle has been initialized.
//     */
//    val isInitialized: Boolean
//        get() = true
//
//    fun setCurrentState(state: Lifecycle.State) {
//        mLifecycleRegistry.currentState = state
//    }
    fun handleLifecycleEvent(event: Lifecycle.Event) {
        mLifecycleRegistry.handleLifecycleEvent(event)
    }
    fun performRestore(savedState: Bundle?) {
        mSavedStateRegistryController.performRestore(savedState)
    }
//    fun performSave(outBundle: Bundle) {
//        mSavedStateRegistryController.performSave(outBundle)
//    }

    override val lifecycle: Lifecycle
        get() = mLifecycleRegistry
    override val savedStateRegistry: SavedStateRegistry
        get() = mSavedStateRegistryController.savedStateRegistry
}
