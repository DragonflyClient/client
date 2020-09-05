package net.inceptioncloud.dragonfly.apps.accountmanager

import net.inceptioncloud.dragonfly.utils.Keep
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

@Keep
class AccountList : ArrayList<Account>()

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