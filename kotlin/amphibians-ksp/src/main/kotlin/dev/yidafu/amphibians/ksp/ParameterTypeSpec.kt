package dev.yidafu.amphibians.ksp

import com.squareup.kotlinpoet.ParameterSpec

const val VAR_NAME_REACT_APPLICATION_CONTEXT = "reactApplicationContext"

val reactApplicationContextParameterSpec =
    ParameterSpec
        .builder(
            VAR_NAME_REACT_APPLICATION_CONTEXT,
            ReactApplicationContextClassName,
        ).build()

val stringNameParameterSpec = ParameterSpec.builder("name", String::class).build()
