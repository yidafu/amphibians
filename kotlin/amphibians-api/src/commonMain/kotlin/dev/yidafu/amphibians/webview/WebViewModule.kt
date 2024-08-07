package dev.yidafu.amphibians.webview

interface WebViewModule {
    @Suppress("ktlint:standard:property-naming")
    abstract val NAME: String

    fun hasNativeMethod(methodName: String): Boolean

    /**
     * call native method
     */
    fun callNative(
        methodName: String,
        vararg args: Any,
    ): Any?
}
