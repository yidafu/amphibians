package dev.yidafu.amphibians.ksp.desc

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSValueParameter
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.ksp.toTypeName
import dev.yidafu.amphibians.annotation.AmphibiansNativeModule
import dev.yidafu.amphibians.ksp.androidModuleClassName
import dev.yidafu.amphibians.ksp.javascriptInterfaceClassName

data class ModuleDesc(
    val classDeclaration: KSClassDeclaration,
    val methods: List<KSFunctionDeclaration>,
) {
    val className: String = classDeclaration.simpleName.asString()
    val androidClassName: String
        get() = className.androidModuleClassName()

    val javascriptInterfaceClassname: String
        get() = className.javascriptInterfaceClassName()

    val packageName: String
        get() = classDeclaration.packageName.asString()

    @OptIn(KspExperimental::class)
    val moduleName: String by lazy {
        classDeclaration.getAnnotationsByType(AmphibiansNativeModule::class).first().name
    }

    val constructorParameters: List<KSValueParameter>
        get() = classDeclaration.primaryConstructor?.parameters ?: emptyList()

    val constructorParametersSpec: List<ParameterSpec> =
        buildList {
            constructorParameters.forEach {
                add(
                    ParameterSpec
                        .builder(
                            it.name?.asString() ?: error("Parameter must have a name"),
                            it.type.toTypeName(),
                        ).build(),
                )
            }
        }

    val typeSpec: ClassName
        get() = ClassName(packageName, androidClassName)

    val memberNAME: PropertySpec
        get() =
            PropertySpec
                .builder("NAME", String::class)
                .initializer(CodeBlock.of("%S", androidClassName))
                .build()
    val reactNativeModuleName: String
        get() {
            return "Native${moduleName}Spec"
        }
    val reactNativeTypeScriptSpecName: String
        get() = "${moduleName}Spec"
}
