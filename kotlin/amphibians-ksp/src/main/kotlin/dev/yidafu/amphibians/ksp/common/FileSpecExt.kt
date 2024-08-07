package dev.yidafu.amphibians.ksp.common

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.symbol.KSFile
import io.outfoxx.typescriptpoet.FileSpec
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets

internal fun FileSpec.writeTo(
    codeGenerator: CodeGenerator,
    dependencies: Dependencies,
) {
    val file = codeGenerator.createNewFileByPath(dependencies, modulePath, extensionName = "ts")

    OutputStreamWriter(file, StandardCharsets.UTF_8)
        .use(::writeTo)
}

fun kspDependencies(
    aggregating: Boolean,
    originatingKSFiles: Iterable<KSFile>,
): Dependencies = Dependencies(aggregating, *originatingKSFiles.toList().toTypedArray())
