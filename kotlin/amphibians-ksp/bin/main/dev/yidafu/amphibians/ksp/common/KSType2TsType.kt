package dev.yidafu.amphibians.ksp.common

import com.google.devtools.ksp.getDeclaredProperties
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSCallableReference
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeParameter
import com.google.devtools.ksp.symbol.KSTypeReference
import com.google.devtools.ksp.symbol.Modifier
import com.google.devtools.ksp.symbol.Origin
import io.outfoxx.typescriptpoet.TypeName

fun KSTypeReference.toTsTypeName(): TypeName {
    val type = resolve()
    return when (element) {
        is KSCallableReference -> {
            TypeName.FUNCTION
        }
        else -> type.toTsTypeName()
    }
}

internal fun KSClassDeclaration.toTsInterface(): TypeName.Anonymous {
    val props = getDeclaredProperties()

    return TypeName.anonymousType(
        props
            .map { prop ->
                TypeName.Anonymous.Member(
                    prop.simpleName.asString(),
                    prop.type.toTsTypeName(),
                    false,
                )
            }.toList(),
    )
}

private fun KSType.toTsTypeName(): TypeName {
    fun resolveTypeArgument(index: Int): TypeName {
        val argType = arguments[index].type
        if (argType != null) {
            return argType.resolve().toTsTypeName()
        }
        error("Could not resolve type argument")
    }
    return when (declaration.qualifiedName?.asString()) {
        "kotlin.Any" -> TypeName.ANY
        "kotlin.Boolean" -> TypeName.BOOLEAN
        "kotlin.Byte",
        "kotlin.Double",
        "kotlin.Float",
        "kotlin.Int",
        "kotlin.Long",
        "kotlin.Number",
        "kotlin.Short",
        -> TypeName.NUMBER
        "kotlin.Char",
        "kotlin.String",
        -> TypeName.STRING
        "kotlin.Unit" -> TypeName.VOID

        "kotlin.Array", "kotlin.collections.List", "kotlin.collections.Set" ->
            TypeName.arrayType(
                resolveTypeArgument(0),
            )
        "kotlin.collections.Map",
        "Kotlin.Map",
        -> TypeName.mapType(resolveTypeArgument(0), resolveTypeArgument(1))
        "kotlin.time.Duration",
        "kotlinx.datetime.Instant",
        "kotlinx.datetime.LocalDate",
        "kotlinx.datetime.LocalDateTime",
        "kotlinx.datetime.LocalTime",
        -> TypeName.DATE
        else -> {
            val decl = declaration
            if (decl.origin != Origin.KOTLIN) {
                // External declarations are not supported and are stubbed with an
                TypeName.ANY
            } else {
                when (decl) {
                    is KSClassDeclaration -> {
                        when (decl.classKind) {
                            ClassKind.INTERFACE -> error("Interfaces are not supported")
                            ClassKind.CLASS -> {
                                if (Modifier.DATA in decl.modifiers) {
                                    decl.toTsInterface()
                                } else if (Modifier.SEALED in decl.modifiers) {
                                    TypeName.NEVER
                                } else {
                                    error("Only data classes and sealed classes are supported, found: $declaration")
                                }
                            }
                            ClassKind.ENUM_CLASS -> error("enum classes are not supported")
                            ClassKind.ENUM_ENTRY -> error("enum entry are not supported")
                            ClassKind.OBJECT -> error("Object are not supported")
                            ClassKind.ANNOTATION_CLASS -> error("Annotation classes are not supported")
                        }
                    }
                    is KSFunctionDeclaration -> {
                        error("Function declarations are not supported")
                    }

                    is KSPropertyDeclaration -> {
                        error("Property declarations are not supported")
                    }

                    is KSTypeParameter -> {
                        // TODO handle bounds
                        TypeName.typeVariable(decl.name.asString())
                    }
                    else -> {
                        error("Unsupported declaration: $decl")
                    }
                }
            }
        }
    }
}
