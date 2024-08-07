package dev.yidafu.amphibians.sample

import com.facebook.react.bridge.ReactApplicationContext
import kotlin.Array
import kotlin.Boolean
import kotlin.Byte
import kotlin.Char
import kotlin.Float
import kotlin.Int
import kotlin.Long
import kotlin.Number
import kotlin.Short
import kotlin.String
import kotlin.Unit
import kotlin.collections.Map

public class SimpleModuleAndroid(
  reactApplicationContext: ReactApplicationContext,
) : NativeSampleSpecSpec(reactApplicationContext) {
  private val mDelegate: SimpleModule = SimpleModule()

  public fun add(a: Int, b: Int): Int = mDelegate.add(a, b)

  public fun argumentExample(
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
//    map: Map<String, String>,
    `data`: DataClass,
  ): Unit = mDelegate.argumentExample(bool, byte, float, int, long, number, short, char, string,
      arrayInt, `data`)

  public companion object {
    public val NAME: String = "SimpleModuleAndroid"
  }
}
