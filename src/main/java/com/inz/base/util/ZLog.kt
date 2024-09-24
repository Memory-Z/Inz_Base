package com.inz.base.util

import android.util.Log

@Suppress("unused")

object ZLog {

    private var TAG = "INZ"
    private var logLevel: Int = Log.VERBOSE
    private var showLog: Boolean = true

    private const val MAX_LOG_LENGTH = 3 * 1024


    fun init(tag: String = TAG, logLevel: Int = Log.VERBOSE, showLog: Boolean = true) {
        this.TAG = tag
        this.logLevel = logLevel
        this.showLog = showLog
    }

    @JvmStatic
    fun i(tag: String, content: String, e: Throwable? = null) {
        splitPrintln(Log.INFO, tag, content, e)
    }

    @JvmStatic
    fun d(tag: String, content: String, e: Throwable? = null) {
        splitPrintln(Log.DEBUG, tag, content, e)
    }

    @JvmStatic
    fun w(tag: String, content: String, e: Throwable? = null) {
        splitPrintln(Log.WARN, tag, content, e)
    }

    @JvmStatic
    fun e(tag: String, content: String, e: Throwable? = null) {
        splitPrintln(Log.ERROR, tag, content, e)
    }

    fun wtf(tag: String, content: String, e: Throwable? = null) {
        splitPrintln(Log.ERROR, tag, content, e)
    }


    private fun splitPrintln(logLevel: Int, tag: String?, content: String, e: Throwable? = null) {
        if (showLog) {
            val length = content.length
            if (length <= MAX_LOG_LENGTH) {
                println(logLevel, tag, content, e)
            } else {
                var msg = content
                while (msg.length > MAX_LOG_LENGTH) {
                    val splitMsg = msg.substring(0, MAX_LOG_LENGTH)
                    println(logLevel, tag, splitMsg, e)
                    msg = msg.removePrefix(splitMsg)
                }
                println(logLevel, tag, msg, e)
            }
        }
    }

    private fun println(logLevel: Int, tag: String?, content: String, e: Throwable? = null) {
        if (showLog) {
            val logTag = "$TAG:::$tag"
            when (logLevel) {
                Log.INFO -> {
                    if (this.logLevel > logLevel) {
                        return
                    }
                    if (e == null) {
                        Log.i(logTag, content)
                    } else {
                        Log.i(logTag, content, e)
                    }
                }

                Log.DEBUG -> {
                    if (this.logLevel > logLevel) {
                        return
                    }
                    if (e == null) {
                        Log.d(logTag, content)
                    } else {
                        Log.d(logTag, content, e)
                    }
                }

                Log.WARN -> {
                    if (this.logLevel > logLevel) {
                        return
                    }
                    if (e == null) {
                        Log.w(logTag, content)
                    } else {
                        Log.w(logTag, content, e)
                    }
                }

                Log.ERROR -> {
                    if (this.logLevel > logLevel) {
                        return
                    }
                    if (e == null) {
                        Log.e(logTag, content)
                    } else {
                        Log.e(logTag, content, e)
                    }
                }

                else -> {
                    if (this.logLevel > logLevel) {
                        return
                    }
                    if (e == null) {
                        Log.v(logTag, content)
                    } else {
                        Log.v(logTag, content, e)
                    }
                }
            }
        }
    }
}