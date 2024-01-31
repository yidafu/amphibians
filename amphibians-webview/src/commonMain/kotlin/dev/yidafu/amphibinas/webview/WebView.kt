package dev.yidafu.amphibinas.webview

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun WebView(
    state: WebViewState,
    modifier: Modifier = Modifier,
    onCreated: () -> Unit = {},
    onDispose: () -> Unit = {},
) {
    ActualWebView(state, modifier, onCreated, onDispose)
}

@Composable
expect fun ActualWebView(
    state: WebViewState,
    modifier: Modifier = Modifier,
    onCreated: () -> Unit = {},
    onDispose: () -> Unit = {},
)
