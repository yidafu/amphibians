package dev.yidafu.amphibians.ksp

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import dev.yidafu.amphibians.ksp.common.descriptor.ClassDescriptor

abstract class AbstractPlatformGenerator(
    internal val codeGenerator: CodeGenerator,
    internal val logger: KSPLogger,
) : PlatformGenerator {
    override fun generate(descriptors: List<ClassDescriptor>) {
        descriptors.forEach { descriptor ->
            generateKotlin(descriptor)
            generateTypeScript(descriptor)
        }
    }
}
