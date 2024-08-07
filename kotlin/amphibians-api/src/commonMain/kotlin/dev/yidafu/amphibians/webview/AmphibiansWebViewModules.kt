package dev.yidafu.amphibians.webview

object AmphibiansWebViewModules {
    private val modules = mutableListOf<WebViewModule>()

    fun addModule(module: WebViewModule) {
        modules.add(module)
    }

    fun callNativeMethod(
        methodName: String,
        vararg args: Any,
    ): Any? {
        modules.forEach { module ->
            if (module.hasNativeMethod(methodName)) {
                return module.callNative(methodName, *args)
            }
        }
        throw IllegalStateException("Native method $methodName not exist.")
    }
}
