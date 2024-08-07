package dev.yidafu.amphibians.ksp.common.descriptor

import com.google.devtools.ksp.symbol.KSValueParameter
import dev.yidafu.amphibians.ksp.common.toDescriptor

data class ParameterDescriptor(
    val name: String,
    val typeDescriptor: TypeDescriptor,
)

fun KSValueParameter.toDescriptor(): ParameterDescriptor {
    val pName = requireNotNull(name).asString()

    return ParameterDescriptor(pName, type.resolve().toDescriptor())
}
