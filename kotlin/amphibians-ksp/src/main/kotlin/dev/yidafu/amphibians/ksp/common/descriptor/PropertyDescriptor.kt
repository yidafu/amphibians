package dev.yidafu.amphibians.ksp.common.descriptor

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec

class PropertyDescriptor(
    val packageName: String,
    val className: String,
    val propName: String,
    val defaultValue: String,
)

fun PropertyDescriptor.toPropertySpec(isPrivate: Boolean = true): PropertySpec {
    val originModule = ClassName(packageName, className)
    return PropertySpec
        .builder(propName, originModule)
        .addModifiers(KModifier.PRIVATE)
        .initializer(
            if (defaultValue.endsWith("()")) {
                CodeBlock.of(defaultValue, originModule)
            } else if (defaultValue.startsWith("\"")) {
                CodeBlock.of("%S", defaultValue.replace("\"", ""))
            } else {
                CodeBlock.of("%L", defaultValue)
            },
        ).build()
}
