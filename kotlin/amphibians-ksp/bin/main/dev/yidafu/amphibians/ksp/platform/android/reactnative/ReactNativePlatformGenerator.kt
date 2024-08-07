package dev.yidafu.amphibians.ksp.platform.android.reactnative

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.joinToCode
import com.squareup.kotlinpoet.ksp.writeTo
import dev.yidafu.amphibians.ksp.AbstractPlatformGenerator
import dev.yidafu.amphibians.ksp.NativeModuleClassName
import dev.yidafu.amphibians.ksp.ReactModuleInfoProviderClassName
import dev.yidafu.amphibians.ksp.TurboReactPackageClassName
import dev.yidafu.amphibians.ksp.androidModuleProviderName
import dev.yidafu.amphibians.ksp.common.descriptor.ClassDescriptor
import dev.yidafu.amphibians.ksp.common.descriptor.toPropertySpec
import dev.yidafu.amphibians.ksp.common.kspDependencies
import dev.yidafu.amphibians.ksp.common.writeTo
import dev.yidafu.amphibians.ksp.platform.android.reactnative.kotlin.propName
import dev.yidafu.amphibians.ksp.platform.android.reactnative.kotlin.toKotlinClass
import dev.yidafu.amphibians.ksp.platform.android.reactnative.kotlin.typeSpec
import dev.yidafu.amphibians.ksp.platform.android.reactnative.typescript.toTypeScriptSpec
import dev.yidafu.amphibians.ksp.reactApplicationContextParameterSpec
import dev.yidafu.amphibians.ksp.stringNameParameterSpec

class ReactNativePlatformGenerator(
    codeGenerator: CodeGenerator,
    logger: KSPLogger,
) : AbstractPlatformGenerator(
        codeGenerator,
        logger,
    ) {
    override fun generate(descriptors: List<ClassDescriptor>) {
        super.generate(descriptors)
        generateProvider(descriptors)
    }

    private fun generateProvider(descriptors: List<ClassDescriptor>) {
        if (descriptors.isEmpty()) return

        val packageName = descriptors[0].packageName
        val providerName = packageName.substringAfterLast(".").androidModuleProviderName()

        val providerClass =
            TypeSpec
                .classBuilder(providerName)
                .apply {
                    superclass(TurboReactPackageClassName)
                    addFunction(
                        FunSpec
                            .builder("getModule")
                            .addModifiers(KModifier.OVERRIDE)
                            .addParameter(stringNameParameterSpec)
                            .addParameter(reactApplicationContextParameterSpec)
                            .returns(NativeModuleClassName.copy(nullable = true))
                            .beginControlFlow("return when(%N)", stringNameParameterSpec)
                            .addCode(
                                descriptors
                                    .map { descriptor ->
                                        CodeBlock.of(
                                            "%T.%N -> %T(%N)",
                                            descriptor.typeSpec,
                                            descriptor.propName.toPropertySpec(),
                                            descriptor.typeSpec,
                                            reactApplicationContextParameterSpec,
                                        )
                                    }.joinToCode(),
                            ).addStatement("else -> null")
                            .endControlFlow()
                            .build(),
                    )

                    addFunction(
                        FunSpec
                            .builder("getReactModuleInfoProvider")
                            .addModifiers(KModifier.OVERRIDE)
                            .returns(ReactModuleInfoProviderClassName.copy(nullable = true))
                            .addStatement("return null")
                            .build(),
                    )
                }.build()
        FileSpec
            .builder(packageName, providerName)
            .addType(providerClass)
            .build()
            .writeTo(codeGenerator, false)
    }

    override fun generateKotlin(descriptor: ClassDescriptor) {
        val ktClass =
            descriptor
                .toKotlinClass()
        logger.info("[Amphibians] Generate React Native Android Module ${descriptor.packageName}.${descriptor.androidClassName}")
        FileSpec
            .builder(descriptor.packageName, descriptor.androidClassName)
            .addType(ktClass)
            .build()
            .writeTo(codeGenerator, false)
    }

    override fun generateTypeScript(descriptor: ClassDescriptor) {
        logger.info("[Amphibians] Generate React Native TypeScript Spec ${descriptor.reactNativeModuleName}")

        val fileSpec = descriptor.toTypeScriptSpec()
        fileSpec.writeTo(
            codeGenerator,
            kspDependencies(true, listOf(descriptor.containingFile)),
        )
    }
}
