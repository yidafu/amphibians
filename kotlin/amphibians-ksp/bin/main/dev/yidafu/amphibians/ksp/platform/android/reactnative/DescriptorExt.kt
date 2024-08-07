package dev.yidafu.amphibians.ksp.platform.android.reactnative

import dev.yidafu.amphibians.ksp.common.descriptor.ClassDescriptor

val ClassDescriptor.reactNativeModuleName: String
    get() = "Native${moduleName}Spec"
