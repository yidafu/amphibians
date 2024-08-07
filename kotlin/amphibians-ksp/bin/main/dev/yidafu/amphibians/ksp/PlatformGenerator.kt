package dev.yidafu.amphibians.ksp

import dev.yidafu.amphibians.ksp.common.descriptor.ClassDescriptor

interface PlatformGenerator {
    fun generateKotlin(descriptor: ClassDescriptor)

    fun generateTypeScript(descriptor: ClassDescriptor)

    fun generate(descriptors: List<ClassDescriptor>)
}
