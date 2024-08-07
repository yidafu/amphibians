package dev.yidafu.amphibians.ksp

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.PlatformInfo
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import dev.yidafu.amphibians.annotation.AmphibiansNativeMethod
import dev.yidafu.amphibians.annotation.AmphibiansNativeModule
import dev.yidafu.amphibians.ksp.common.descriptor.ClassDescriptor
import dev.yidafu.amphibians.ksp.platform.android.reactnative.ReactNativePlatformGenerator
import dev.yidafu.amphibians.ksp.platform.android.webview.WebViewPlatformGenerator

// private const val JvmPlatform = "JVM"
// private const val NativePlatform = "Native"

internal data class NativeModule(
    val wrapperClassDeclaration: KSClassDeclaration,
    val moduleName: String,
    val methods: List<KSFunctionDeclaration>,
)

class AmphibiansProcessor(
    val codeGenerator: CodeGenerator,
    private val platforms: List<PlatformInfo>,
    val logger: KSPLogger,
) : SymbolProcessor {
    private val moduleAnnotationName: String
        get() = requireNotNull(AmphibiansNativeModule::class.qualifiedName)
    private val methodAnnotationName: String
        get() = requireNotNull(AmphibiansNativeMethod::class.qualifiedName)

    private fun findAllAmphibiansModuleMethodMap(resolver: Resolver): Map<KSClassDeclaration, List<KSFunctionDeclaration>> =
        resolver
            .getSymbolsWithAnnotation(methodAnnotationName)
            .map { annotatedNode ->
                when (annotatedNode) {
                    is KSFunctionDeclaration ->
                        annotatedNode.also {
                            if (it.typeParameters.isNotEmpty()) {
                                error("Type Parameters are not supported for AmphibiansNativeMethod")
                            }
                        }
                    else -> throw IllegalArgumentException("AmphibiansNativeMethod annotation can only be used on function declaration.")
                }
            }.groupBy { annotatedNode ->
                annotatedNode.parentDeclaration.let {
                    when (it) {
                        is KSClassDeclaration -> it
                        else -> throw IllegalArgumentException("AmphibiansNativeMethod must be declared in a class")
                    }
                }
            }

    private fun findAllAmphibiansModules(resolver: Resolver): List<KSClassDeclaration> {
//        val platformNames = platforms.map { it.platformName }

        return resolver
            .getSymbolsWithAnnotation(moduleAnnotationName)
            .map { node ->
                when (node) {
                    is KSClassDeclaration ->
                        node.also {
                            if (it.typeParameters.isNotEmpty()) {
                                error("Type parameters are not supported for AmphibiansNativeModule")
                            }
                        }
                    else -> error("AmphibiansNativeModule annotation can only be used on class declarations")
                }
            }.toList()
    }

    /**
     * 1. collect module information ( native module and information )
     * 2. create intermediate data class
     * 3. generate platform code ( Android / iOS / WebView JSAPI )
     */
    @OptIn(KspExperimental::class)
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val platformNames = platforms.map { it.platformName }
        logger.warn("platforms ==> ${platformNames.joinToString(", ")}")

        val moduleMethodMap = findAllAmphibiansModuleMethodMap(resolver)
        val classDescriptorList =
            findAllAmphibiansModules(resolver)
                .map { wrappedClassDeclaration ->
                    val methods = moduleMethodMap[wrappedClassDeclaration] ?: emptyList()

                    wrappedClassDeclaration.containingFile
                    ClassDescriptor.create(wrappedClassDeclaration, methods)
                }

        listOf(
            ReactNativePlatformGenerator(codeGenerator, logger),
            WebViewPlatformGenerator(codeGenerator, logger),
        ).forEach { generator ->
            generator.generate(classDescriptorList)
        }

//        classDescriptorList.forEach { module ->
//            val hasModule = module.classDeclaration.isAnnotationPresent(AmphibiansNativeModule::class)
//            logger.warn("has module $hasModule")
//
//            val classSpec = module.toReactNativeAndroidModule()
//
//            FileSpec
//                .builder(module.packageName, module.androidClassName)
//                .addType(classSpec)
//                .build()
//                .writeTo(codeGenerator, false)
//
//            val tsFileSPec = module.toTypeScriptSpec()
//            tsFileSPec.writeTo(codeGenerator, kspDependencies(true, listOf(module.classDeclaration.containingFile!!)))
//
//            val jsInterface = module.toWebViewJavaScriptInterface()
//            FileSpec
//                .builder(module.packageName, module.javascriptInterfaceClassname)
//                .addType(jsInterface)
//                .build()
//                .writeTo(codeGenerator, false)
//        }

        if (classDescriptorList.isEmpty()) return emptyList()

//        val providerDesc = ModuleProviderDesc(classDescriptorList)

//        val packageSpec = providerDesc.toReactNativeAndroidModuleProvider()
//        FileSpec
//            .builder(providerDesc.packageName, providerDesc.providerName)
//            .addType(packageSpec)
//            .build()
//            .writeTo(codeGenerator, false)
        return emptyList()
    }
}
