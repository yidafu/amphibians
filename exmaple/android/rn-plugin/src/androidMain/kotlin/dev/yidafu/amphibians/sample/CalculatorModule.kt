package dev.yidafu.amphibians.sample

import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import dev.yidafu.amphibians.annotation.AmphibiansNativeMethod
import dev.yidafu.amphibians.annotation.AmphibiansNativeModule

class CalculatorModule internal constructor(
    context: ReactApplicationContext?,
) : NativeCalculatorSpec(context) {
    override fun add(
        a: Double,
        b: Double,
        promise: Promise?,
    ) {
        promise?.resolve(a + b)
    }

    companion object {
        const val NAME: String = "RNCalculator"
    }
}


data class DataClass(
    val foo: String,
    val bar: Int,
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
        byte: Byte,
        float: Float,
        int: Int,
        long: Long,
        number: Number,
        short: Short,
        char: Char,
        string: String,
        arrayInt: Array<Int>,
        map: Map<String, String>,
        data: DataClass,
    ) {
    }
}
