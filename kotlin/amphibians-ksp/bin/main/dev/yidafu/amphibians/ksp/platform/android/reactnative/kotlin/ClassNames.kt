package dev.yidafu.amphibians.ksp.platform.android.reactnative.kotlin

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.MemberName

val PromiseClassName = ClassName("com.facebook.react.bridge", "Promise")
val CoroutineScopeClassName = ClassName("kotlinx.coroutines", "CoroutineScope")
val DispatchersClassName = ClassName("kotlinx.coroutines", "Dispatchers")

val launchMemberName = MemberName("kotlinx.coroutines", "launch")

// react native bridge type
val ReadableNativeArray = ClassName("com.facebook.react.bridge", "ReadableNativeArray")
val NativeArray = ClassName("com.facebook.react.bridge", "NativeArray")
val WritableNativeArray = ClassName("com.facebook.react.bridge", "WritableNativeArray")

val ReadableNativeMap = ClassName("com.facebook.react.bridge", "ReadableNativeMap")
val NativeMap = ClassName("com.facebook.react.bridge", "NativeMap")
val WritableNativeMap = ClassName("com.facebook.react.bridge", "WritableNativeMap")
