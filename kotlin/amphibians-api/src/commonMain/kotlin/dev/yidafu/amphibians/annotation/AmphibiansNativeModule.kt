package dev.yidafu.amphibians.annotation

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class AmphibiansNativeModule(val name: String)
