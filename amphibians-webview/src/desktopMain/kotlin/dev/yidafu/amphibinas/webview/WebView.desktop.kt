package dev.yidafu.amphibinas.webview

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
actual fun ActualWebView(
    state: WebViewState,
    modifier: Modifier,
    onCreated: () -> Unit,
    onDispose: () -> Unit,
) {
}
