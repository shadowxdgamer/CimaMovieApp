package com.shadowxdgamer.movieapp.ui.screens

import android.app.Activity
import android.content.pm.ActivityInfo
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import java.io.ByteArrayInputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreen(embedUrl: String) {
    val context = LocalContext.current
    var isFullScreen by remember { mutableStateOf(false) }

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
                isFullScreen = true

                val activity = context as? Activity ?: return
                val decorView = activity.window.decorView as FrameLayout
                decorView.addView(customView, FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
                setFullScreen(activity, true)
            }

            override fun onHideCustomView() {
                val activity = context as? Activity ?: return
                val decorView = activity.window.decorView as FrameLayout
                decorView.removeView(customView)
                customView = null
                customViewCallback?.onCustomViewHidden()
                customViewCallback = null
                isFullScreen = false
                setFullScreen(activity, false)
            }

            private fun setFullScreen(activity: Activity, enabled: Boolean) {
                val window = activity.window
                val insetsController = WindowCompat.getInsetsController(window, window.decorView)
                insetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                if (enabled) {
                    activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                    insetsController.hide(WindowInsetsCompat.Type.systemBars())
                } else {
                    activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                    insetsController.show(WindowInsetsCompat.Type.systemBars())
                }
            }
        }
    }

    BackHandler(enabled = isFullScreen) {
        chromeClient.onHideCustomView()
    }

    Scaffold(
        topBar = {
            if (!isFullScreen) {
                TopAppBar(title = { Text("Now Playing") })
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            AndroidView(
                factory = {
                    WebView(it).apply {
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        settings.mediaPlaybackRequiresUserGesture = false
                        webChromeClient = chromeClient
                        webViewClient = AdBlockingWebViewClient()
                        loadUrl(embedUrl)
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            chromeClient.onHideCustomView()
        }
    }
}

private class AdBlockingWebViewClient : WebViewClient() {
    private val AD_HOSTS = setOf(
        "admob.com", "doubleclick.net", "googlesyndication.com",
        "google-analytics.com", "adservice.google.com", "app-measurement.com"
    )

    override fun shouldInterceptRequest(view: WebView?, request: WebResourceRequest?): WebResourceResponse? {
        val requestUrl = request?.url?.host ?: ""
        if (AD_HOSTS.any { host -> requestUrl.contains(host) }) {
            return WebResourceResponse("text/plain", "utf-8", ByteArrayInputStream("".toByteArray()))
        }
        return super.shouldInterceptRequest(view, request)
    }

    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        val requestUrl = request?.url?.toString() ?: return false
        if (requestUrl.startsWith("http://") || requestUrl.startsWith("https://")) {
            return false
        }
        return true
    }
}