package dev.yidafu.amphibians.ksp

internal fun String.androidModuleClassName() = this + "Android"

internal fun String.javascriptInterfaceClassName() = this + "WebView"

internal fun String.androidModuleProviderName() = this[0].uppercaseChar() + this.substring(1) + "Provider"
