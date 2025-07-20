package com.shadowxdgamer.movieapp.ui.screens

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

@Composable
fun PlayerScreen(embedUrl: String, onBackClick: () -> Unit) {
    val context = LocalActivity.current as Activity
    var isCustomViewVisible by remember { mutableStateOf(false) }

    // The DisposableEffect that forced landscape has been removed.

    val chromeClient = remember {
        object : WebChromeClient() {
            private var customView: View? = null
            private var customViewCallback: CustomViewCallback? = null

            override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
                if (customView != null) {
                    onHideCustomView()
                    return
                }
                customView = view
                customViewCallback = callback
                isCustomViewVisible = true

                val decorView = context.window.decorView as FrameLayout
                decorView.addView(
                    customView,
                    FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                )
                // Hide system bars when web content requests fullscreen
                val window = context.window
                WindowCompat.getInsetsController(window, window.decorView).apply {
                    hide(WindowInsetsCompat.Type.systemBars())
                    systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                }
            }

            override fun onHideCustomView() {
                val decorView = context.window.decorView as FrameLayout
                decorView.removeView(customView)
                customView = null
                customViewCallback?.onCustomViewHidden()
                customViewCallback = null
                isCustomViewVisible = false

                // Show system bars when web content exits fullscreen
                val window = context.window
                WindowCompat.getInsetsController(window, window.decorView).show(WindowInsetsCompat.Type.systemBars())
            }
        }
    }

    // Handle the Android back button press only when the web player's custom view is showing
    BackHandler(enabled = isCustomViewVisible) {
        chromeClient.onHideCustomView()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = {
                WebView(it).apply {
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    settings.mediaPlaybackRequiresUserGesture = true
                    webChromeClient = chromeClient
                    webViewClient = WebViewClient()
                    loadUrl(embedUrl)
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // Our custom back button, always visible in the top-left corner
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.White
            )
        }
    }
}
