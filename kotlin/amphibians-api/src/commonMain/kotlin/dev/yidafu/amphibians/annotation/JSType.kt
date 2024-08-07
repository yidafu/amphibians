package dev.yidafu.amphibians.annotation


@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.TYPE)
annotation class JSType(val identifier: String)