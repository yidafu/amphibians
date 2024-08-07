package dev.yidafu.amphibians.webview

import dev.yidafu.amphibians.callback.Callback

class WebViewCallback(
    val cbId: String,
) : Callback {
    override fun success(result: String) {
    }

    override fun fail(e: Throwable) {
    }
}
