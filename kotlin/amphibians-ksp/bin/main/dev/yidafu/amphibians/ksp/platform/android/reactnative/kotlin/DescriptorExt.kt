package dev.yidafu.amphibians.ksp.platform.android.reactnative.kotlin

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
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
import dev.yidafu.amphibians.ksp.platform.android.reactnative.reactNativeModuleName
import dev.yidafu.amphibians.ksp.reactApplicationContextParameterSpec

fun createPromiseParameter() = ParameterSpec.builder("promise", PromiseClassName).build()

fun ClassDescriptor.toKotlinClass(): TypeSpec =
    TypeSpec
        .classBuilder(androidClassName)
        .apply {
            superclass(ClassName(packageName, reactNativeModuleName))

            addSuperclassConstructorParameter(CodeBlock.of("%N", reactApplicationContextParameterSpec))

            primaryConstructor(
                FunSpec
                    .constructorBuilder()
                    .addParameter(reactApplicationContextParameterSpec)
                    .build(),
            )

            addProperty(propName.toPropertySpec())

            methods.forEach { method ->
                addFunction(
                    method
                        .toKotlinFunction()
                        .toBuilder()
                        .addModifiers(KModifier.OVERRIDE)
                        .build(),
                )
            }

            properties.forEach { addProperty(it.toPropertySpec()) }
        }.build()

val ClassDescriptor.typeSpec: ClassName
    get() = ClassName(packageName, androidClassName)

val ClassDescriptor.propName: PropertyDescriptor
    get() = PropertyDescriptor("kotlin", "String", "NAME", androidClassName)

fun TypeDescriptor.toTypeName(): TypeName =
    when (this) {
        is TypeDescriptor.Collection -> mainClass.asClassName().parameterizedBy(childClass.toTypeName())
        is TypeDescriptor.DataClass -> className
        is TypeDescriptor.Map ->
            Map::class.asClassName().parameterizedBy(keyType.toTypeName(), valueType.toTypeName())
        is TypeDescriptor.Primary -> {
            klass.asClassName()
        }
        is TypeDescriptor.TypeParameter -> TODO()
        TypeDescriptor.Void -> UNIT
    }

fun TypeDescriptor.toReactNativeTypeName(): TypeName =
    when (this) {
        is TypeDescriptor.Collection -> ReadableNativeArray
        is TypeDescriptor.DataClass -> ReadableNativeMap
        is TypeDescriptor.Map -> ReadableNativeMap
        is TypeDescriptor.Primary -> toTypeName()
        is TypeDescriptor.TypeParameter -> TODO()
        TypeDescriptor.Void -> UNIT
    }

fun TypeDescriptor.toReactNativeType(): String =
    when (this) {
        is TypeDescriptor.Collection -> "Array"
        is TypeDescriptor.DataClass -> "Map"
        is TypeDescriptor.Map -> "Map"
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

fun FunctionDescriptor.toKotlinFunction() =
    FunSpec
        .builder(name)
        .apply {
            val descriptors = arguments
            val rnParameters = descriptors.map { it.toReactNativeParameterSpec() }

            addParameters(rnParameters)

            val promiseParam = createPromiseParameter()
            if (isSuspend) {
                addParameter(promiseParam)
            }
            descriptors
                .filter {
                    (it.typeDescriptor is TypeDescriptor.DataClass)
                }.forEach {
                    it.typeDescriptor as TypeDescriptor.DataClass
                    val constructorList =
                        it.typeDescriptor.properties
                            .map { prop ->
                                // TODO: set default prop value
                                CodeBlock.of(
                                    "%N = %N.get%L(\"%N\")",
                                    prop.key,
                                    it.name,
                                    prop.value.toReactNativeType(),
                                    prop.key,
                                )
                            }.joinToCode()
                    addStatement(
                        """
                        val %L_dataclass = %T(
                            %L
                        );
                        """.trimIndent(),
                        it.name,
                        it.typeDescriptor.className,
                        constructorList,
                    )
                }
            if (isSuspend) {
                returns(UNIT)
                /**
                 * generate code like:
                 * ```kt
                 *     public fun promiseFunction(promise: Promise) {
                 *         CoroutineScope(Dispatchers.Main).launch {
                 *             try {
                 *                 promise.resolve(mDelegate.promiseFunction())
                 *             } catch (e: Exception) {
                 *                 promise.reject(e)
                 *             }
                 *         }
                 *     }
                 * ```
                 */
                addStatement(
                    """
                    %T(%T.%N).%M {
                        try {
                            %N.resolve(%N.%N(%L))
                        } catch (e: Exception) {
                            %N.reject(e)
                        }
                    }
                    """.trimIndent(),
                    CoroutineScopeClassName,
                    DispatchersClassName,
                    coroutineContext.toString(),
                    launchMemberName,
                    promiseParam,
                    delegate.toPropertySpec(),
                    name,
                    descriptors.map { it.toNativeMethodArgumentType() }.joinToCode(),
                    promiseParam,
                )
            } else {
                returns(returnTYpe.toTypeName())

                addStatement(
                    "return %N.%N(%L)",
                    delegate.toPropertySpec(),
                    name,
                    descriptors.map { it.toNativeMethodArgumentType() }.joinToCode(),
                )
            }
        }.build()

//    FunSpec
//        .builder(name)
//        .apply {
//            val descriptors = arguments
//            val rnParameters = descriptors.map { it.toReactNativeParameterSpec() }
//
//            addParameters(rnParameters)
//
//            val promiseParam = createPromiseParameter()
//            if (isSuspend) {
//                addParameter(promiseParam)
//            }
//            descriptors
//                .filter {
//                    (it.typeDescriptor is TypeDescriptor.DataClass)
//                }.forEach {
//                    it.typeDescriptor as TypeDescriptor.DataClass
//                    val constructorList =
//                        it.typeDescriptor.properties
//                            .map { prop ->
//                                // TODO: set default prop value
//                                CodeBlock.of(
//                                    "%N = %N.get%L(\"%N\")",
//                                    prop.key,
//                                    it.name,
//                                    prop.value.toReactNativeType(),
//                                    prop.key,
//                                )
//                            }.joinToCode()
//                    addStatement(
//                        """
//                        val %L_dataclass = %T(
//                            %L
//                        );
//                        """.trimIndent(),
//                        it.name,
//                        it.typeDescriptor.className,
//                        constructorList,
//                    )
//                }
//            if (isSuspend) {
//                returns(UNIT)
//                /**
//                 * generate code like:
//                 * ```kt
//                 *     public fun promiseFunction(promise: Promise) {
//                 *         CoroutineScope(Dispatchers.Main).launch {
//                 *             try {
//                 *                 promise.resolve(mDelegate.promiseFunction())
//                 *             } catch (e: Exception) {
//                 *                 promise.reject(e)
//                 *             }
//                 *         }
//                 *     }
//                 * ```
//                 */
//                addStatement(
//                    """
//                    %T(%T.%N).%M {
//                        try {
//                            %N.resolve(%N.%N(%L))
//                        } catch (e: Exception) {
//                            %N.reject(e)
//                        }
//                    }
//                    """.trimIndent(),
//                    CoroutineScopeClassName,
//                    DispatchersClassName,
//                    coroutineContext.toString(),
//                    launchMemberName,
//                    promiseParam,
//                    delegate.toPropertySpec(),
//                    name,
//                    descriptors.map { it.toNativeMethodArgumentType() }.joinToCode(),
//                    promiseParam,
//                )
//            } else {
//                returns(returnTYpe.toTypeName())
//
//                addStatement(
//                    "return %N.%N(%L)",
//                    delegate.toPropertySpec(),
//                    name,
//                    descriptors.map { it.toNativeMethodArgumentType() }.joinToCode(),
//                )
//            }
//        }.build()

fun ParameterDescriptor.toNativeMethodArgumentType(): CodeBlock =
    when (val descriptor = typeDescriptor) {
        is TypeDescriptor.Collection -> {
            when (descriptor.mainClass.qualifiedName) {
                Array::class.qualifiedName -> {
                    CodeBlock.of("(%N.toArrayList() as List<%T>).toTypedArray()", name, descriptor.childClass.toTypeName())
                }
                Set::class.qualifiedName -> {
                    CodeBlock.of("%N.toArrayList().toSet()", name)
                }
                List::class.qualifiedName -> {
                    CodeBlock.of("%N.toArrayList()", name)
                }
                else -> error("Collection must be Array/Set/List")
            }
        }

        is TypeDescriptor.DataClass -> CodeBlock.of("%L_dataclass", name)
        is TypeDescriptor.Map ->
            CodeBlock.of(
                "%N.toHashMap() as Map<%T, %T>",
                name,
                descriptor.keyType.toTypeName(),
                descriptor.valueType.toTypeName(),
            )
        is TypeDescriptor.Primary -> CodeBlock.of("%N", name)
        is TypeDescriptor.TypeParameter -> TODO()
        TypeDescriptor.Void -> TODO()
    }

fun ParameterDescriptor.toReactNativeParameterSpec() =
    ParameterSpec
        .builder(name, typeDescriptor.toReactNativeTypeName())
        .build()
