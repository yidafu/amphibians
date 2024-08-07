package dev.yidafu.amphibians.callback

interface Callback {
    fun success(result: String)

    fun fail(e: Throwable)
}
