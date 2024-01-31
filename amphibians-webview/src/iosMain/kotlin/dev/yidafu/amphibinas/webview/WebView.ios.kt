package dev.yidafu.amphibinas.webview

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.readValue
import platform.CoreGraphics.CGRectZero
import platform.Foundation.setValue
import platform.WebKit.WKWebView
import platform.WebKit.WKWebViewConfiguration
import platform.WebKit.javaScriptEnabled

@Composable
actual fun ActualWebView(
    state: WebViewState,
    modifier: Modifier,
    onCreated: () -> Unit,
    onDispose: () -> Unit,
) {
    IOSWebView(state, Modifier, onCreated, onDispose)
}

@OptIn(ExperimentalForeignApi::class)
@Composable
fun IOSWebView(
    state: WebViewState,
    modifier: Modifier,
    onCreated: () -> Unit,
    onDispose: () -> Unit,
) {
    UIKitView(
        factory = {
            val config = WKWebViewConfiguration().apply {
                defaultWebpagePreferences.allowsContentJavaScript = true
                preferences.apply {
                    setValue(true, forKey = "allowFileAccessFromFileURLs")
                    javaScriptEnabled = true
                }
            }
            WKWebView(
                frame = CGRectZero.readValue(),
                configuration = config,
            ).apply {
                setOpaque(false)
                onCreated()
            }
        },
        modifier = modifier,
        onRelease = {},
    )
}
