package dev.yidafu.amphibians.ksp.common.descriptor

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import dev.yidafu.amphibians.annotation.AmphibiansNativeModule
import dev.yidafu.amphibians.ksp.androidModuleClassName
import dev.yidafu.amphibians.ksp.javascriptInterfaceClassName

class ClassDescriptor(
    val packageName: String,
    val className: String,
    val moduleName: String,
    val methods: List<FunctionDescriptor>,
    val properties: List<PropertyDescriptor>,
    val containingFile: KSFile,
) {
    val androidClassName: String
        get() = className.androidModuleClassName()

    val webviewClassname: String
        get() = className.javascriptInterfaceClassName()

    companion object {
        @OptIn(KspExperimental::class)
        fun create(
            classDeclaration: KSClassDeclaration,
            methods: List<KSFunctionDeclaration>,
        ): ClassDescriptor {
            val moduleName = classDeclaration.getAnnotationsByType(AmphibiansNativeModule::class).first().name

            val className: String = classDeclaration.simpleName.asString()

            val packageName: String = classDeclaration.packageName.asString()

            val delegate = PropertyDescriptor(packageName, className, "mDelegate", "%T()")

            return ClassDescriptor(
                packageName,
                className,
                moduleName,
                methods.map { it.toDescriptor(delegate) },
                listOf(delegate),
                classDeclaration.containingFile!!,
            )
        }
    }
}
