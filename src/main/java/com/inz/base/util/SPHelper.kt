package com.inz.base.util

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.Serializable
import java.lang.ref.SoftReference

@Suppress("unused")
object SPHelper {

    private const val DEFAULT_SP_FILE = "shared_preferences"
    private var contextSoftReference: SoftReference<Context>? = null

    private val buildMap = mutableMapOf<String, Builder>()

    /**
     * Get SP Instance.
     */
    fun getInstance(value: String = DEFAULT_SP_FILE): Builder? {
        var builder: Builder? = null
        synchronized(SPHelper::class) {
            if (buildMap.containsKey(value)) {
                builder = buildMap[value]
            } else {
                contextSoftReference?.let {
                    it.get()?.let { context ->
                        builder = Builder(context, value)
                            .let { builder ->
                                buildMap.put(value, builder)
                            }
                    }
                }
            }
        }
        return builder
    }

    fun init(context: Context) {
        contextSoftReference = SoftReference(context)
    }


    @Suppress("unused", "MemberVisibilityCanBePrivate")
    class Builder(context: Context, name: String) {

        private val sharedPreferences: SharedPreferences


        init {
            sharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE)
        }

        fun putString(key: String, value: String) {
            sharedPreferences.edit().putString(key, value).apply()
        }

        fun getString(key: String, defValue: String = "") =
            sharedPreferences.getString(key, defValue)

        fun putStringSet(key: String, value: Set<String>) {
            sharedPreferences.edit().putStringSet(key, value).apply()
        }

        fun getStringSet(key: String, value: Set<String>? = null): Set<String>? =
            sharedPreferences.getStringSet(key, value)

        fun putInt(key: String, value: Int) {
            sharedPreferences.edit().putInt(key, value).apply()
        }

        fun getInt(key: String, defValue: Int = 0): Int = sharedPreferences.getInt(key, defValue)

        fun putLong(key: String, value: Long) {
            sharedPreferences.edit().putLong(key, value).apply()
        }

        fun getLong(key: String, defValue: Long = 0): Long =
            sharedPreferences.getLong(key, defValue)

        fun putBoolean(key: String, value: Boolean) {
            sharedPreferences.edit().putBoolean(key, value).apply()
        }

        fun getBoolean(key: String, defValue: Boolean = false): Boolean =
            sharedPreferences.getBoolean(key, defValue)

        fun putFloat(key: String, value: Float) {
            sharedPreferences.edit().putFloat(key, value).apply()
        }

        fun getFloat(key: String, defValue: Float = 0F): Float =
            sharedPreferences.getFloat(key, defValue)

        fun <T : Serializable> putSerializable(key: String, value: T) {
            val valueStr = Gson().toJson(value)
            putString(key, valueStr)
        }

        fun <T : Serializable> getSerializable(key: String, clazz: Class<T>): T? {
            val valueStr = getString(key, "")
            valueStr?.let {
                return Gson().fromJson(it, TypeToken.get(clazz))
            }
            return null
        }
    }
}