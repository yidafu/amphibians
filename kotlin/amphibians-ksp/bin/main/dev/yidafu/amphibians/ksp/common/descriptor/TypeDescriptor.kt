package dev.yidafu.amphibians.ksp.common.descriptor

import com.squareup.kotlinpoet.ClassName
import kotlin.reflect.KClass

sealed class TypeDescriptor {
    class Primary(
        val klass: KClass<*>,
    ) : TypeDescriptor()

    class Collection(
        val mainClass: KClass<*>,
        val childClass: TypeDescriptor,
    ) : TypeDescriptor()

    class Map(
        val keyType: TypeDescriptor,
        val valueType: TypeDescriptor,
    ) : TypeDescriptor()

    class DataClass(
        val className: ClassName,
        val properties: kotlin.collections.Map<String, TypeDescriptor>,
    ) : TypeDescriptor()

    object Void : TypeDescriptor()

    class TypeParameter : TypeDescriptor()
}
