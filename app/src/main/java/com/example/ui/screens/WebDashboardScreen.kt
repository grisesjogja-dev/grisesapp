package com.example.ui.screens

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.print.PrintAttributes
import android.print.PrintManager
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebDashboardScreen(
  modifier: Modifier = Modifier,
  onSwitchToNative: (() -> Unit)? = null,
  onOpenTab: ((Int) -> Unit)? = null
) {
  var webViewInstance by remember { mutableStateOf<WebView?>(null) }
  var canGoBackState by remember { mutableStateOf(false) }

  // Handle hardware / gesture back button to navigate history inside WebView
  BackHandler(enabled = canGoBackState) {
    if (webViewInstance?.canGoBack() == true) {
      webViewInstance?.goBack()
    }
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(Color(0xFF008F5D))
      .statusBarsPadding()
  ) {
    // Top Bar for Web Mode
    Surface(
      color = Color(0xFF008F5D),
      shadowElevation = 2.dp,
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
              .size(32.dp)
              .clip(CircleShape)
              .background(Color.White.copy(alpha = 0.2f))
          ) {
            Icon(
              imageVector = Icons.Default.Public,
              contentDescription = null,
              tint = Color.White,
              modifier = Modifier.size(18.dp)
            )
          }
          Spacer(modifier = Modifier.width(8.dp))
          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "GRISES",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 15.sp,
                color = Color.White
              )
              Spacer(modifier = Modifier.width(6.dp))
              Surface(
                shape = RoundedCornerShape(4.dp),
                color = Color(0xFF10B981)
              ) {
                Text(
                  text = "VERSI WEB",
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color.White,
                  modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                )
              }
            }
            Text(
              text = "Perum Griya Sedayu Selaras",
              fontSize = 10.sp,
              color = Color.White.copy(alpha = 0.85f)
            )
          }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          // Cetak / PDF Button
          IconButton(
            onClick = {
              webViewInstance?.evaluateJavascript("if (typeof triggerPrintReport === 'function') { triggerPrintReport(); } else { window.print(); }", null)
            },
            modifier = Modifier
              .size(34.dp)
              .testTag("web_quick_print_btn")
          ) {
            Icon(
              imageVector = Icons.Default.Print,
              contentDescription = "Cetak / Simpan PDF",
              tint = Color.White,
              modifier = Modifier.size(18.dp)
            )
          }

          // Reload button
          IconButton(
            onClick = { webViewInstance?.reload() },
            modifier = Modifier
              .size(34.dp)
              .testTag("web_reload_btn")
          ) {
            Icon(
              imageVector = Icons.Default.Refresh,
              contentDescription = "Muat Ulang",
              tint = Color.White,
              modifier = Modifier.size(18.dp)
            )
          }

          // Gemini AI Assistant Button
          if (onOpenTab != null) {
            IconButton(
              onClick = { onOpenTab(1) },
              modifier = Modifier
                .size(34.dp)
                .testTag("web_open_ai_btn")
            ) {
              Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = "Asisten AI Gemini",
                tint = Color(0xFFFDE047),
                modifier = Modifier.size(19.dp)
              )
            }
          }

          // Switch to Native App Button
          if (onSwitchToNative != null) {
            Spacer(modifier = Modifier.width(4.dp))
            Surface(
              shape = RoundedCornerShape(16.dp),
              color = Color.White.copy(alpha = 0.2f),
              modifier = Modifier
                .testTag("switch_to_native_btn")
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                  .padding(horizontal = 10.dp, vertical = 6.dp)
              ) {
                FilledTonalButton(
                  onClick = onSwitchToNative,
                  contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                  modifier = Modifier.size(height = 28.dp, width = 110.dp)
                ) {
                  Icon(
                    imageVector = Icons.Default.Apps,
                    contentDescription = null,
                    modifier = Modifier.size(13.dp)
                  )
                  Spacer(modifier = Modifier.width(4.dp))
                  Text("Ke Native", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
              }
            }
          }
        }
      }
    }

    // WebView Host
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(Color.White)
    ) {
      AndroidView(
        factory = { context ->
          WebView(context).apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.allowFileAccess = true
            settings.allowContentAccess = true
            settings.loadWithOverviewMode = true
            settings.useWideViewPort = true
            settings.setSupportZoom(true)
            settings.builtInZoomControls = false

            webChromeClient = WebChromeClient()

            // Expose Native Android Print, Share, and AI Navigation APIs to JavaScript
            addJavascriptInterface(
              AndroidPrintBridge(
                context = context,
                webViewProvider = { webViewInstance ?: this },
                onOpenTab = onOpenTab
              ),
              "AndroidBridge"
            )

            webViewClient = object : WebViewClient() {
              override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                canGoBackState = view?.canGoBack() == true
              }

              override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                val url = request?.url?.toString() ?: ""
                // Open WhatsApp or external phone/mail links in native apps
                if (url.startsWith("https://api.whatsapp.com") ||
                    url.startsWith("whatsapp://") ||
                    url.startsWith("tel:") ||
                    url.startsWith("mailto:")
                ) {
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
            webViewInstance = this
          }
        },
        update = { webView ->
          webViewInstance = webView
        },
        modifier = Modifier.fillMaxSize()
      )
    }
  }
}

/**
 * JavaScript Interface bridge to connect WebView with native Android PrintManager
 * and system share intent (WhatsApp, Email, etc.)
 */
class AndroidPrintBridge(
  private val context: Context,
  private val webViewProvider: () -> WebView?,
  private val onOpenTab: ((Int) -> Unit)? = null
) {
  @JavascriptInterface
  fun printDocument(title: String) {
    (context as? ComponentActivity)?.runOnUiThread {
      val webView = webViewProvider() ?: return@runOnUiThread
      val printManager = context.getSystemService(Context.PRINT_SERVICE) as? PrintManager
      if (printManager != null) {
        val printAdapter = webView.createPrintDocumentAdapter(title)
        val cleanJobName = title.replace("[^a-zA-Z0-9_-]".toRegex(), "_")
        printManager.print(cleanJobName, printAdapter, PrintAttributes.Builder().build())
      } else {
        Toast.makeText(context, "Layanan cetak tidak tersedia di perangkat ini", Toast.LENGTH_SHORT).show()
      }
    }
  }

  @JavascriptInterface
  fun shareSummaryText(text: String, title: String) {
    (context as? ComponentActivity)?.runOnUiThread {
      try {
        val sendIntent = Intent().apply {
          action = Intent.ACTION_SEND
          putExtra(Intent.EXTRA_TEXT, text)
          putExtra(Intent.EXTRA_SUBJECT, title)
          type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, title)
        shareIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(shareIntent)
      } catch (e: Exception) {
        Toast.makeText(context, "Gagal membagikan laporan: ${e.message}", Toast.LENGTH_SHORT).show()
      }
    }
  }

  @JavascriptInterface
  fun openAiAssistant() {
    (context as? ComponentActivity)?.runOnUiThread {
      onOpenTab?.invoke(1) // 1 is Asisten AI
    }
  }

  @JavascriptInterface
  fun openNativeTab(tabIndex: Int) {
    (context as? ComponentActivity)?.runOnUiThread {
      onOpenTab?.invoke(tabIndex)
    }
  }
}

