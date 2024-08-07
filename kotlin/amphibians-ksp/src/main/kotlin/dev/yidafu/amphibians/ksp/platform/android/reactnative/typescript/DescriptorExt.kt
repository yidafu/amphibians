package dev.yidafu.amphibians.ksp.platform.android.reactnative.typescript

import dev.yidafu.amphibians.ksp.common.descriptor.ClassDescriptor
import dev.yidafu.amphibians.ksp.common.descriptor.FunctionDescriptor
import dev.yidafu.amphibians.ksp.common.descriptor.ParameterDescriptor
import dev.yidafu.amphibians.ksp.common.descriptor.TypeDescriptor
import dev.yidafu.amphibians.ksp.platform.android.reactnative.reactNativeModuleName
import io.outfoxx.typescriptpoet.CodeBlock
import io.outfoxx.typescriptpoet.FileSpec
import io.outfoxx.typescriptpoet.FunctionSpec
import io.outfoxx.typescriptpoet.InterfaceSpec
import io.outfoxx.typescriptpoet.Modifier
import io.outfoxx.typescriptpoet.ParameterSpec
import io.outfoxx.typescriptpoet.TypeAliasSpec
import io.outfoxx.typescriptpoet.TypeName

fun ClassDescriptor.toTypeScriptSpec(): FileSpec {
    val iSpec =
        InterfaceSpec
            .builder("Spec")
            .apply {
                addSuperInterface(TurboModuleClass)
                methods.forEach { method ->
                    addFunction(method.toTsFunction())
                }
            }.build()
    return FileSpec
        .builder(reactNativeModuleName)
        .apply {
            addInterface(iSpec)
            addTypeAlias(TypeAliasSpec.builder("r", RegistryClassName).addTSDoc("trigger named import").build())
            addCode(
                CodeBlock.of("export default %N.get<Spec>(\"$androidClassName\") as Spec | null", RegistryClassName),
            )
        }.build()
}

fun TypeDescriptor.toTsTypeName(): TypeName =
    when (this) {
        is TypeDescriptor.Collection -> TypeName.arrayType(childClass.toTsTypeName())
        is TypeDescriptor.DataClass -> {
            TypeName.anonymousType(
                properties.map { prop ->
                    TypeName.Anonymous.Member(
                        prop.key,
                        prop.value.toTsTypeName(),
                        false,
                    )
                },
            )
        }
        is TypeDescriptor.Map ->
            TypeName.MAP.parameterized(
                keyType.toTsTypeName(),
                valueType.toTsTypeName(),
            )
        is TypeDescriptor.Primary -> {
            when (klass) {
                Byte::class,
                Short::class,
                Int::class,
                Float::class,
                Double::class,
                Long::class,
                Number::class,
                -> TypeName.NUMBER
                Boolean::class -> TypeName.BOOLEAN
                String::class -> TypeName.STRING
                Char::class -> TypeName.NUMBER
                else -> error("Unsupported primary type ${klass.qualifiedName}")
            }
        }
        is TypeDescriptor.TypeParameter -> TODO()
        TypeDescriptor.Void -> TypeName.VOID
    }

fun ParameterDescriptor.toTsTypeName(): ParameterSpec = ParameterSpec.builder(name, typeDescriptor.toTsTypeName()).build()

fun FunctionDescriptor.toTsFunction() =
    FunctionSpec
        .builder(name)
        .addModifiers(Modifier.ABSTRACT)
        .addParameters(arguments.map { it.toTsTypeName() })
        .build()
