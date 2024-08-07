package dev.yidafu.amphibians.ksp.common.descriptor

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.Modifier
import dev.yidafu.amphibians.annotation.AmphibiansNativeMethod
import dev.yidafu.amphibians.annotation.CoroutinesContext
import dev.yidafu.amphibians.ksp.common.toDescriptor

class FunctionDescriptor(
    val name: String,
    val isSuspend: Boolean = false,
    val arguments: List<ParameterDescriptor>,
    val returnTYpe: TypeDescriptor,
    val coroutineContext: CoroutinesContext,
    val delegate: PropertyDescriptor,
)

// @OptIn(KspExperimental::class)
// val KSFunctionDeclaration.coroutineContext: CoroutinesContext
//    get() =
//        getAnnotationsByType(AmphibiansNativeMethod::class).firstOrNull()?.context
//            ?: CoroutinesContext.Main
//
// fun createPromiseParameter() = ParameterSpec.builder("promise", PromiseClassName).build()

@OptIn(KspExperimental::class)
fun KSFunctionDeclaration.toDescriptor(delegate: PropertyDescriptor): FunctionDescriptor {
    val name = simpleName.asString()

    val isSuspend = modifiers.firstOrNull { modifier -> modifier == Modifier.SUSPEND } != null

    val arguments = parameters.map { it.toDescriptor() }
    val rType = returnType?.resolve()?.toDescriptor() ?: TypeDescriptor.Void
    val coroutineContext =
        getAnnotationsByType(AmphibiansNativeMethod::class).firstOrNull()?.context
            ?: CoroutinesContext.Main
    return FunctionDescriptor(name, isSuspend, arguments, rType, coroutineContext, delegate)
}
