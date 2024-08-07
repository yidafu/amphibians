package dev.yidafu.amphibians.annotation

enum class CoroutinesContext {
    Main,
    Default,
    Unconfined,
    IO,
    ;

    override fun toString(): String =
        when (this) {
            Main -> "Main"
            Default -> "Default"
            Unconfined -> "Unconfined"
            IO -> "IO"
        }
}

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.FUNCTION)
annotation class AmphibiansNativeMethod(
    val context: CoroutinesContext = CoroutinesContext.Main,
)
