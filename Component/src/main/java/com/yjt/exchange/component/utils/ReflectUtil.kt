package com.hynet.heebit.components.utils

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class ReflectUtil {

    companion object {

        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            ReflectUtil()
        }

    }

    fun getGenericSuperclassType(subclass: Class<*>): Type {
        return getGenericSuperclassTypes(0, subclass)
    }

    private fun getGenericSuperclassTypes(index: Int, subclass: Class<*>): Type {
        val superclass = subclass.genericSuperclass as? ParameterizedType ?: return Any::class.java
        val params = superclass.actualTypeArguments
        if (index >= params.size || index < 0) {
            throw RuntimeException("Index outof bounds")
        }
        return if (params[index] !is Class<*>) {
            Any::class.java
        } else params[index]
    }
}