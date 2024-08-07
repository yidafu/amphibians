package dev.yidafu.amphibians.ksp.common

import com.google.devtools.ksp.getDeclaredProperties
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeParameter
import com.google.devtools.ksp.symbol.Modifier
import com.google.devtools.ksp.symbol.Origin
import com.squareup.kotlinpoet.ksp.toClassName
import dev.yidafu.amphibians.ksp.common.descriptor.TypeDescriptor

fun KSType.toDescriptor(): TypeDescriptor {
    fun resolveTypeArgument(index: Int): TypeDescriptor {
        val argType = arguments[index].type
        if (argType != null) {
            return argType.resolve().toDescriptor()
        }
        error("Could not resolve type argument")
    }

    val qualifiedName =
        declaration.qualifiedName
            ?.asString()

    return when (qualifiedName) {
        "kotlin.Any" -> TypeDescriptor.Primary(Any::class)
        "kotlin.Boolean" -> TypeDescriptor.Primary(Boolean::class)
        "kotlin.Byte" -> TypeDescriptor.Primary(Byte::class)
        "kotlin.Double" -> TypeDescriptor.Primary(Double::class)
        "kotlin.Float" -> TypeDescriptor.Primary(Float::class)
        "kotlin.Int" -> TypeDescriptor.Primary(Int::class)
        "kotlin.Long" -> TypeDescriptor.Primary(Long::class)
        "kotlin.Number" -> TypeDescriptor.Primary(Number::class)
        "kotlin.Short" -> TypeDescriptor.Primary(Short::class)
        "kotlin.Char" -> TypeDescriptor.Primary(Char::class)
        "kotlin.String" -> TypeDescriptor.Primary(String::class)
        "kotlin.Unit" -> TypeDescriptor.Primary(Unit::class)
        "kotlin.Array" -> TypeDescriptor.Collection(Array::class, resolveTypeArgument(0))
        "kotlin.collections.List" -> TypeDescriptor.Collection(List::class, resolveTypeArgument(0))
        "kotlin.collections.Set" -> TypeDescriptor.Collection(Set::class, resolveTypeArgument(0))

        "kotlin.collections.Map" -> TypeDescriptor.Map(resolveTypeArgument(0), resolveTypeArgument(1))

        else -> {
            val decl = declaration
            if (decl.origin == Origin.KOTLIN) {
                when (decl) {
                    is KSClassDeclaration -> {
                        when (decl.classKind) {
                            ClassKind.CLASS -> {
                                if (Modifier.DATA in decl.modifiers) {
                                    val props = decl.getDeclaredProperties()

                                    TypeDescriptor.DataClass(
                                        toClassName(),
                                        props
                                            .map { prop ->
                                                prop.simpleName.asString() to prop.type.resolve().toDescriptor()
                                            }.toMap(),
                                    )
                                } else {
                                    error("Only data class")
                                }
                            }
                            else -> error("Only class is supported")
                        }
                    }
                    is KSTypeParameter -> {
                        TypeDescriptor.TypeParameter()
                    }
                    else -> error("Unsupported declaration $decl")
                }
            } else {
                // external declaration not supported
                error("Unsupported external declaration $qualifiedName")
            }
        }
    }
}
