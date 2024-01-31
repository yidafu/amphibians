package dev.yidafu.amphibinas.webview

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
@Composable
actual fun ActualWebView(
    state: WebViewState,
    modifier: Modifier,
    onCreated: () -> Unit,
    onDispose: () -> Unit,
) {
    AndroidWebViewView(state, modifier, onCreated, onDispose)
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun AndroidWebViewView(
    state: WebViewState,
    modifier: Modifier,
    onCreated: () -> Unit,
    onDestroy: () -> Unit,
) {
    val webView = remember { mutableStateOf<WebView?>(null) }
    val client: WebViewClient = remember { AmphibiansWebViewClient() }
    val chromeClient = remember { AmphibiansWebChromeClient() }
    LaunchedEffect(Unit) {
        webView.value?.apply {
            state.url?.let { url ->
                loadUrl(url)
            }
        }
    }

    AndroidView(factory = { context ->
        onCreated()
        WebView(context).apply {
            webViewClient = client
            webChromeClient = chromeClient
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
            )
            settings.apply {
                javaScriptEnabled = true
            }
            webView.value = this
        }
    }, modifier)

    DisposableEffect(Unit) {
        onDispose { onDestroy() }
    }
}
