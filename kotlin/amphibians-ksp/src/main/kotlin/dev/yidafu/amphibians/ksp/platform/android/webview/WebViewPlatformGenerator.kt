package dev.yidafu.amphibians.ksp.platform.android.webview

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.ksp.writeTo
import dev.yidafu.amphibians.ksp.AbstractPlatformGenerator
import dev.yidafu.amphibians.ksp.common.descriptor.ClassDescriptor
import dev.yidafu.amphibians.ksp.common.kspDependencies
import dev.yidafu.amphibians.ksp.platform.android.webview.kotlin.toKotlinClass
import dev.yidafu.amphibians.ksp.platform.android.webview.typescript.jsapiModuleName
import dev.yidafu.amphibians.ksp.platform.android.webview.typescript.toTsFunction
import io.outfoxx.typescriptpoet.ClassSpec
import io.outfoxx.typescriptpoet.CodeBlock
import io.outfoxx.typescriptpoet.FunctionSpec
import io.outfoxx.typescriptpoet.Modifier
import io.outfoxx.typescriptpoet.ParameterSpec
import io.outfoxx.typescriptpoet.TypeName
import dev.yidafu.amphibians.ksp.common.writeTo as cWriteTo

class WebViewPlatformGenerator(
    codeGenerator: CodeGenerator,
    logger: KSPLogger,
) : AbstractPlatformGenerator(
        codeGenerator,
        logger,
    ) {
    override fun generate(descriptors: List<ClassDescriptor>) {
        super.generate(descriptors)

        createJsapi(descriptors)
    }

    override fun generateKotlin(descriptor: ClassDescriptor) {
        val ktClass = descriptor.toKotlinClass()
        logger.info("[Amphibians] Generate WebView JSAPI Class ${descriptor.packageName}.${descriptor.webviewClassname}")
        FileSpec
            .builder(descriptor.packageName, descriptor.webviewClassname)
            .addType(ktClass)
            .build()
            .writeTo(codeGenerator, false)
    }

    override fun generateTypeScript(descriptor: ClassDescriptor) {
        logger.info("[Amphibians] Generate WebView JSAPI TypeScript ${descriptor.jsapiModuleName}")
//        descriptor
//            .toTypeScriptSpec()
//            .writeTo(
//                codeGenerator,
//                kspDependencies(true, listOf(descriptor.containingFile)),
//            )
    }

    private fun createJsapi(descriptors: List<ClassDescriptor>) {
        if (descriptors.isEmpty()) return

        val methods =
            descriptors
                .map {
                    it.methods
                }.flatten()

        io.outfoxx.typescriptpoet.FileSpec
            .builder("AmphibiansNativeApi")
            .apply {
                addClass(
                    ClassSpec
                        .builder("AmphibiansNativeApi")
                        .apply {
                            methods.forEach {
                                addFunction(it.toTsFunction())
                            }

                            addFunction(
                                FunctionSpec
                                    .builder("callNative")
                                    .apply {
                                        addParameter(
                                            ParameterSpec.builder("methodName", TypeName.STRING).build(),
                                        )

                                        restParameter(
                                            ParameterSpec.builder("args", TypeName.ANY).build(),
                                        )
                                        addModifiers(Modifier.PRIVATE)
                                        addStatement("return window.AmphibiansNativeApi.callNative(methodName, *args)")
                                    }.build(),
                            )
                        }.build(),
                )
                addCode(CodeBlock.of("window.native = AmphibiansNativeApi()"))
            }.build()
            .cWriteTo(codeGenerator, kspDependencies(true, listOf(descriptors[0].containingFile)))
    }
}
