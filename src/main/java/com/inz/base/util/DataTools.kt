package com.inz.base.util

import android.os.Build
import android.os.Bundle
import com.google.gson.Gson
import java.io.Serializable
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * Data Tools.
 *
 * -------------------------
 * @author Administrator
 * create Date: 2024/6/1 8:36 by zheng
 */
@Suppress("unused")
object DataTools {


    fun <T : Serializable> getSerializableDataByBundle(
        bundle: Bundle,
        key: String,
        clazz: Class<T>
    ): T? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            bundle.getSerializable(key, clazz)
        } else {
            @Suppress("UNCHECKED_CAST")
            try {
                @Suppress("DEPRECATION")
                bundle.getSerializable(key) as T?
            } catch (ignore: Exception) {
                null
            }
        }
    }

    fun <T : Serializable> getStringByData(data: T): String {
        return Gson().toJson(data)
    }

    fun <T : Serializable> getSerializableByString(context: String, clazz: Class<T>): T? {
        return try {
            Gson().fromJson(context, clazz)
        } catch (ignore: Exception) {
            return null
        }
    }

    fun <T : Serializable> getStringByDataList(data: List<T>): String {
        return Gson().toJson(data)
    }

    fun <T : Serializable> getSerializableDataListByString(
        context: String,
        clazz: Class<T>
    ): List<T>? {
        return try {
            val gson = Gson()
            val parameterizedType = ParameterizedTypeImpl(clazz, arrayOf(clazz), clazz)
            gson.fromJson(context, parameterizedType)
        } catch (ignore: Exception) {
            null
        }
    }

    private class ParameterizedTypeImpl<T>(
        val raw: Class<T>,
        val args: Array<Type> = arrayOf(),
        val owner: Type?
    ) : ParameterizedType {
        override fun getActualTypeArguments(): Array<Type> {
            return args
        }

        override fun getRawType(): Type {
            return raw
        }

        override fun getOwnerType(): Type? {
            return owner
        }
    }

}