package com.example.ui.screens

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebDashboardScreen(modifier: Modifier = Modifier) {
  Box(modifier = modifier.fillMaxSize()) {
    AndroidView(
      factory = { context ->
        WebView(context).apply {
          settings.javaScriptEnabled = true
          settings.domStorageEnabled = true
          settings.allowFileAccess = true
          settings.loadWithOverviewMode = true
          settings.useWideViewPort = true

          webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
              val url = request?.url?.toString() ?: ""
              // Open WhatsApp or external links in native apps
              if (url.startsWith("https://api.whatsapp.com") || url.startsWith("whatsapp://")) {
                try {
                  val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                  }
                  context.startActivity(intent)
                  return true
                } catch (e: Exception) {
                  return false
                }
              }
              return false
            }
          }

          loadUrl("file:///android_asset/web/index.html")
        }
      },
      modifier = Modifier.fillMaxSize()
    )
  }
}
