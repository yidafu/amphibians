package dev.yidafu.amphibians.ksp

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

class AmphibiansProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return AmphibiansProcessor(
            environment.codeGenerator,
            environment.platforms,
            environment.logger,
        )
    }
}
