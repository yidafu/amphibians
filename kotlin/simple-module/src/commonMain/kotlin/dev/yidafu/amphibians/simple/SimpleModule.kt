package dev.yidafu.amphibians.simple

import dev.yidafu.amphibians.annotation.AmphibiansNativeMethod
import dev.yidafu.amphibians.annotation.AmphibiansNativeModule
import dev.yidafu.amphibians.annotation.CoroutinesContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class DataClass(
    val foo: String?,
    val bar: Int?,
)

/**
 * should generate code like
 * @ReactNativeModule
 * class SimpleModuleAndroid {
 *      @ReactNativeModule
 *      fun add(a: Int, b: Int) = a + b
 * }
 */
@AmphibiansNativeModule("Simple")
class SimpleModule {
    @AmphibiansNativeMethod
    fun add(
        a: Int,
        b: Int,
    ): Int = a + b

    @AmphibiansNativeMethod
    fun argumentExample(
        bool: Boolean,
        b: Byte,
        f: Float,
        i: Int,
        long: Long,
        num: Number,
        s: Short,
        c: Char,
        str: String,
        arrayInt: Array<Int>,
        map: Map<String, String>,
        dataKlass: DataClass,
    ) {
        Json {
            encodeDefaults = true
        }
    }

    @AmphibiansNativeMethod(context = CoroutinesContext.IO)
    suspend fun promiseFunction(a: Int): Int =
        withContext(Dispatchers.IO) {
            delay(100)
            1 + a
        }
}
