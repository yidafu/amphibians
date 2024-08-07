package dev.yidafu.amphibians.ksp.desc

import dev.yidafu.amphibians.ksp.androidModuleProviderName

class ModuleProviderDesc(
//    val packageName: String,
    val modules: List<ModuleDesc>,
) {
    val packageName: String
        get() = if (modules.isNotEmpty()) modules[0].packageName else ""
    val providerName: String
        get() {
//            throw IllegalArgumentException(packageName)
            return packageName.substringAfterLast(".").androidModuleProviderName()
        }
}
