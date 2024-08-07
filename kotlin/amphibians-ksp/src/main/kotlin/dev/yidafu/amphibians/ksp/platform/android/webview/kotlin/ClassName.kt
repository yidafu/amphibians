package dev.yidafu.amphibians.ksp.platform.android.webview.kotlin

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.ParameterSpec

val JavascriptInterfaceClassName = ClassName("android.webkit", "JavascriptInterface")

val JsonClassName = ClassName("import kotlinx.serialization.json", "Json")

val CallbackClassName = ClassName("dev.yidafu.amphibians.callback", "Callback")

val encodeToStringMember = MemberName("kotlinx.serialization", "encodeToString")
// import kotlinx.serialization.encodeToString

val WebViewModuleClassName = ClassName("dev.yidafu.amphibians.webview", "WebViewModule")

val callbackParameter = ParameterSpec.builder("callback", CallbackClassName).build()
val methodNameParameterSpec = ParameterSpec.builder("methodName", String::class).build()

val WebViewCallbackClassName = ClassName("dev.yidafu.amphibians.webview", "WebViewCallback")
