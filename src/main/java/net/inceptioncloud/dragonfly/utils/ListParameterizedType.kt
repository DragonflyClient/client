package net.inceptioncloud.dragonfly.utils

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * Represents a type token for a List with a parameterized type that isn't
 * erased at runtime and thus eliminates issues when parsing lists via Gson.
 */
class ListParameterizedType (private val type: Type) : ParameterizedType {

    override fun getActualTypeArguments(): Array<Type> {
        return arrayOf(type)
    }

    override fun getRawType(): Type? {
        return ArrayList::class.java
    }

    override fun getOwnerType(): Type? {
        return null
    }
}