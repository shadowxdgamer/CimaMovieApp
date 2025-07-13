package com.shadowxdgamer.movieapp.ui.screens

import android.content.Intent
import android.os.Message
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import java.io.ByteArrayInputStream
import java.net.URISyntaxException

// A basic list of known ad-related hosts. A real app would use a much larger, updated list.
private val AD_HOSTS = setOf(
    "admob.com",
    "doubleclick.net",
    "googlesyndication.com",
    "google-analytics.com",
    "adservice.google.com",
    "app-measurement.com",
    "ak.amskiploomr.com" // The ad domain from your error log
)

@Composable
fun WebViewPlayer(url: String, modifier: Modifier = Modifier) {
    AndroidView(
        factory = { context ->
            WebView(context).apply {
                // --- AD BLOCKING & POP-UP CONTROL ---
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.loadWithOverviewMode = true
                settings.useWideViewPort = true
                settings.cacheMode = WebSettings.LOAD_NO_CACHE

                // 1. Block Pop-up Windows
                settings.setSupportMultipleWindows(true) // Enable support for multiple windows
                webChromeClient = object : WebChromeClient() {
                    // Reject all requests to create a new window (pop-up)
                    override fun onCreateWindow(
                        view: WebView?, isDialog: Boolean, isUserGesture: Boolean, resultMsg: Message?
                    ): Boolean {
                        return false
                    }
                }

                webViewClient = object : WebViewClient() {
                    // 2. Block Requests to Ad Servers
                    override fun shouldInterceptRequest(
                        view: WebView?,
                        request: WebResourceRequest?
                    ): WebResourceResponse? {
                        val requestUrl = request?.url?.host ?: ""
                        // If the request URL's host is in our ad list, block it
                        if (AD_HOSTS.any { host -> requestUrl.contains(host) }) {
                            // Return an empty response to block the request
                            return WebResourceResponse("text/plain", "utf-8", ByteArrayInputStream("".toByteArray()))
                        }
                        return super.shouldInterceptRequest(view, request)
                    }

                    // 3. Keep Navigation Inside the App
                    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                        val requestUrl = request?.url?.toString() ?: return false

                        // If it's an Intent URL, handle it manually
                        if (requestUrl.startsWith("intent://")) {
                            try {
                                val intent = Intent.parseUri(requestUrl, Intent.URI_INTENT_SCHEME)
                                // Look for a fallback URL to load in the current WebView
                                val fallbackUrl = intent.getStringExtra("browser_fallback_url")
                                if (fallbackUrl != null) {
                                    view?.loadUrl(fallbackUrl)
                                    return true // We've handled it
                                }
                                // If no fallback, do nothing and prevent the intent from launching
                                return true
                            } catch (e: URISyntaxException) {
                                e.printStackTrace()
                                return true // Prevent error page
                            }
                        }

                        // For normal http/https links, let the WebView handle it
                        if (requestUrl.startsWith("http://") || requestUrl.startsWith("https://")) {
                            return false
                        }

                        // For other schemes, block them to prevent leaving the app
                        return true
                    }
                }
                loadUrl(url)
            }
        },
        update = { it.loadUrl(url) },
        modifier = modifier
    )
}