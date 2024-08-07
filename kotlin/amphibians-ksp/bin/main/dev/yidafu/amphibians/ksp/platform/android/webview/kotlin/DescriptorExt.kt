package dev.yidafu.amphibians.ksp.platform.android.webview.kotlin

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.STRING
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.UNIT
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.joinToCode
import dev.yidafu.amphibians.ksp.common.descriptor.ClassDescriptor
import dev.yidafu.amphibians.ksp.common.descriptor.FunctionDescriptor
import dev.yidafu.amphibians.ksp.common.descriptor.ParameterDescriptor
import dev.yidafu.amphibians.ksp.common.descriptor.PropertyDescriptor
import dev.yidafu.amphibians.ksp.common.descriptor.TypeDescriptor
import dev.yidafu.amphibians.ksp.common.descriptor.toPropertySpec
import dev.yidafu.amphibians.ksp.platform.android.reactnative.kotlin.CoroutineScopeClassName
import dev.yidafu.amphibians.ksp.platform.android.reactnative.kotlin.DispatchersClassName
import dev.yidafu.amphibians.ksp.platform.android.reactnative.kotlin.launchMemberName
import dev.yidafu.amphibians.ksp.platform.android.reactnative.kotlin.toTypeName

val ClassDescriptor.jsapiName: PropertyDescriptor
    get() = PropertyDescriptor("kotlin", "String", "NAME", "\"$moduleName\"")

val jsonProp: PropertyDescriptor
    get() = PropertyDescriptor("kotlinx.serialization.json", "Json", "json", "Json { encodeDefaults = true }")

fun ClassDescriptor.toKotlinClass(): TypeSpec =
    TypeSpec
        .classBuilder(webviewClassname)
        .apply {
            addSuperinterface(WebViewModuleClassName)

            addProperty(
                PropertySpec
                    .builder(
                        "NAME",
                        String::class,
                    ).addModifiers(KModifier.OVERRIDE)
                    .initializer(CodeBlock.of("%S", moduleName))
                    .build(),
//                jsapiName
//                    .toPropertySpec(),
//                    .toBuilder()
//                    .addModifiers(KModifier.OVERRIDE)
//                    .build(),
            )
            addProperty(jsonProp.toPropertySpec())

            addFunction(
                FunSpec
                    .builder("hasNativeMethod")
                    .apply {
                        addModifiers(KModifier.OVERRIDE)

                        addParameter(methodNameParameterSpec)

                        addStatement(
                            "return listOf(${
                                methods.joinToString(",") { "\"${it.name}\"" }
                            }).contains(methodName)",
                        )

                        returns(Boolean::class)
                    }.build(),
            )

            addFunction(
                FunSpec
                    .builder("callNative")
                    .apply {
                        addModifiers(KModifier.OVERRIDE)
                        addParameter(methodNameParameterSpec)
                        addParameter(
                            ParameterSpec
                                .builder("args", Any::class)
                                .addModifiers(KModifier.VARARG)
                                .build(),
                        )

                        beginControlFlow("return when (%N)", methodNameParameterSpec)
                        methods.forEach { method ->
                            val argList =
                                List(method.arguments.size) { index ->
                                    CodeBlock.of("arg_$index")
                                }.toMutableList()

                            addCode(
                                CodeBlock
                                    .builder()
                                    .apply {
                                        addCode("%S -> ", method.name)
                                        beginControlFlow("")
                                        method.arguments.forEachIndexed { index, arg ->
                                            addStatement("val arg_$index = args[$index] as %T", arg.typeDescriptor.toWebViewTypeName())
                                        }

                                        if (method.isSuspend) {
                                            addStatement("val cbId = args[${method.arguments.size}] as String")
                                            addStatement("val cb = %T(cbId)", WebViewCallbackClassName)
                                            argList.add(CodeBlock.of("cb"))
                                        }
                                        addStatement(
                                            "%N(%L)",
                                            method.name,
                                            argList.joinToCode(),
                                        )
                                        endControlFlow()
                                    }.build(),
                            )
                        }
                        addStatement("else -> error(\"method $%N is missing.\")", methodNameParameterSpec)
                        endControlFlow()
                        returns(Any::class)
                    }.build(),
            )

            methods.forEach { method ->
                val ktFun =
                    method
                        .toKotlinFunction()
                        .toBuilder()
                        .apply {
                            addAnnotation(JavascriptInterfaceClassName)
                        }.build()
                addFunction(ktFun)
            }

            properties.forEach { addProperty(it.toPropertySpec()) }
        }.build()

fun ParameterDescriptor.toTransformStatement(index: Int): CodeBlock {
    val newName = "${name}_$index"
    return when (val descriptor = typeDescriptor) {
        is TypeDescriptor.Collection -> {
            when (descriptor.mainClass) {
                List::class -> {
                    CodeBlock.of(
                        "val %N: List<%T> = %N.decodeFromString(%N)",
                        newName,
                        descriptor.childClass.toTypeName(),
                        jsonProp.toPropertySpec(),
                        name,
                    )
                }
                Set::class -> {
                    CodeBlock.of(
                        "val %N: Set<%T> = %N.decodeFromString(%N)",
                        newName,
                        descriptor.childClass.toTypeName(),
                        jsonProp.toPropertySpec(),
                        name,
                    )
                }
                Array::class -> {
                    CodeBlock.of(
                        "val %N: Array<%T> = %N.decodeFromString(%N)",
                        newName,
                        descriptor.childClass.toTypeName(),
                        jsonProp.toPropertySpec(),
                        name,
                    )
                }
                else -> {
                    error("Collection type only can be List/Set/Array")
                }
            }
        }
        is TypeDescriptor.DataClass ->
            CodeBlock.of(
                "val %N: %T = %N.decodeFromString<%T>(%N)",
                newName,
                descriptor.toTypeName(),
                jsonProp.toPropertySpec(),
                descriptor.toTypeName(),
                name,
            )
        is TypeDescriptor.Map -> {
            CodeBlock.of(
                "val %N: Map<%T, %T> = %N.decodeFromString(%N)",
                newName,
                descriptor.keyType.toTypeName(),
                descriptor.valueType.toTypeName(),
                jsonProp.toPropertySpec(),
                name,
            )
        }
        is TypeDescriptor.Primary -> {
            CodeBlock.of("val %N = %N", newName, name)
        }

        is TypeDescriptor.TypeParameter -> TODO()
        TypeDescriptor.Void -> TODO()
    }
}

fun FunctionDescriptor.toKotlinFunction() =
    FunSpec
        .builder(name)
        .apply {
            val descriptors = arguments
            val wvParameters = descriptors.map { it.toWebViewParameterSpec() }
            addParameters(wvParameters)

            // add parameter transform glue code
            descriptors.forEachIndexed { index, param ->
                addCode("«")
                addCode(param.toTransformStatement(index))
                addCode("\n»")
            }

            val callbackPara = callbackParameter
            if (isSuspend) {
                addParameter(callbackPara)
            }

            if (isSuspend) {
                returns(UNIT)

                addStatement(
                    """
                    %T(%T.%N).%M {
                        try {
                            val result = %N.%N(%L)
                            %N.success(%N.%M(result))
                        } catch (e: Exception) {
                            %N.fail(e)
                        }
                    }
                    """.trimIndent(),
                    // line 1
                    CoroutineScopeClassName,
                    DispatchersClassName,
                    coroutineContext.toString(),
                    launchMemberName,
                    // line 3
                    delegate.toPropertySpec(),
                    name,
                    descriptors.toIndexedArguments(),
                    // line 4
                    callbackPara,
                    jsonProp.toPropertySpec(),
                    encodeToStringMember,
                    // line 6
                    callbackPara,
                )
            } else {
                returns(returnTYpe.toTypeName())

                addStatement(
                    "return %N.%N(%L)",
                    delegate.toPropertySpec(),
                    name,
                    descriptors.toIndexedArguments(),
                )
            }
        }.build()

fun ParameterDescriptor.toWebViewParameterSpec() =
    ParameterSpec
        .builder(name, typeDescriptor.toWebViewTypeName())
        .build()

fun TypeDescriptor.toWebViewTypeName(): TypeName =
    when (this) {
        is TypeDescriptor.Collection -> STRING
        is TypeDescriptor.DataClass -> STRING
        is TypeDescriptor.Map -> STRING
        is TypeDescriptor.Primary -> klass.asClassName()
        is TypeDescriptor.TypeParameter -> TODO()
        TypeDescriptor.Void -> UNIT
    }

/**
 * only primary type is recommended
 * https://chromium.googlesource.com/chromium/src/+/refs/heads/main/android_webview/docs/java-bridge.md#the-api
 */
fun TypeDescriptor.toWebViewType(): String =
    when (this) {
        is TypeDescriptor.Collection -> "String"
        is TypeDescriptor.DataClass -> "String"
        is TypeDescriptor.Map -> "String"
        is TypeDescriptor.Primary -> {
            when (klass) {
                Byte::class,
                Short::class,
                Int::class,
                -> "Int"
                Float::class,
                Double::class,
                Long::class,
                -> "Double"
                Boolean::class -> "Boolean"
                String::class -> "String"
                Char::class -> "Int"
                else -> error("Unsupported primary type")
            }
        }
        is TypeDescriptor.TypeParameter -> TODO()
        TypeDescriptor.Void -> "Unit"
    }

fun List<ParameterDescriptor>.toIndexedArguments() =
    mapIndexed { index, param ->
        CodeBlock.of("%N_$index", param.name)
    }.joinToCode()
