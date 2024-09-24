package com.inz.base.crash

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Looper
import android.os.Process
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.PrintWriter
import java.io.StringWriter
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.system.exitProcess

/**
 * Crash Handler.
 */
class CrashHandler : Thread.UncaughtExceptionHandler {

    companion object {
        private const val LOG_PREFIX: String = "INZ"


        @SuppressLint("StaticFieldLeak")
        private var crashHandler: CrashHandler? = null

        fun getInstance(
            context: Context,
            prefix: String = LOG_PREFIX,
            crashHandlerListener: CrashHandlerListener? = null
        ): CrashHandler {
            synchronized(CrashHandler::class) {
                if (crashHandler == null) {
                    crashHandler = CrashHandler()
                }
            }
            crashHandler!!.init(context, prefix, crashHandlerListener)
            return crashHandler!!
        }

        /**
         * get Crash File Dir Path.
         */
        fun getCrashFileDirPath(context: Context): String {
            var dir = context.externalCacheDir
            if (dir == null) {
                dir = context.cacheDir
            }
            dir!!.let { file ->
                if (!file.exists()) {
                    file.mkdirs()
                }
                val crashDir = File(file, "crash")
                if (!crashDir.exists()) {
                    crashDir.mkdirs()
                }
                return crashDir.absolutePath
            }
        }
    }


    private lateinit var mContext: Context
    private lateinit var dateFormat: DateFormat
    private var logPrefix: String = LOG_PREFIX

    private var versionName: String = ""
    private var versionCode: String = ""
    private var uncaughtExceptionHandler: Thread.UncaughtExceptionHandler? = null
    private val logIndoMap: LinkedHashMap<String, Any> = linkedMapOf()
    private var crashHandlerListener: CrashHandlerListener? = null


    fun init(
        context: Context,
        logPrefix: String = LOG_PREFIX,
        crashHandlerListener: CrashHandlerListener? = null
    ) {
        this.mContext = context
        this.crashHandlerListener = crashHandlerListener
        this.logPrefix = logPrefix
        this.uncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler(crashHandler)
        dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())
    }


    override fun uncaughtException(t: Thread, e: Throwable) {
        crashHandlerListener?.startHandler()
        if (!handleException(mContext, e) && uncaughtExceptionHandler != null) {
            uncaughtExceptionHandler!!.uncaughtException(t, e)
        } else {
            try {
                Thread.sleep(3000L)
            } catch (ignore: Exception) {
            }
            Process.killProcess(Process.myPid())
            exitProcess(1)
        }
    }

    private fun handleException(context: Context, e: Throwable): Boolean {
        Thread({
            Looper.prepare()
            crashHandlerListener?.showErrorTintUi()
            Looper.loop()
        }, "CRASH_HANDLER_THREAD").start()
        // get device info
        collectDeviceInfo(context)
        // save log info
        val filePath = saveCrashInfo2File(context, logPrefix, e, versionName, versionCode)
        return filePath.isNotEmpty()
    }

    /**
     * Collect Device Info.
     */
    private fun collectDeviceInfo(context: Context) {
        val packageManager = context.packageManager
        val packageInfo =
            packageManager.getPackageInfo(context.packageName, PackageManager.GET_ACTIVITIES)
        val versionName = packageInfo.versionName
        val versionCode = packageInfo.longVersionCode
        logIndoMap["versionName"] = versionName
        logIndoMap["versionCode"] = versionCode

        this.versionName = versionName
        this.versionCode = versionCode.toString()

        val fields = Build::class.java.fields
        fields.forEach { field ->
            field.isAccessible = true
            val obj = field.get(null)
            logIndoMap[field.name] = obj?.toString() ?: "NAN"
        }
    }

    /**
     * Save Crash Info to File.
     */
    private fun saveCrashInfo2File(
        context: Context,
        prefix: String,
        e: Throwable,
        versionName: String,
        versionCode: String
    ): String {
        val stringBuilder = StringBuilder()
        logIndoMap.entries.forEach {
            val key = it.key
            val value = it.value
            stringBuilder.append(key).append(" = ").append(value).append(" , \r\n")
        }
        val calendar = Calendar.getInstance(Locale.getDefault())
        stringBuilder
            .append("------------------------- CRASH WRITER TIME -------------------------")
            .append(dateFormat.format(calendar.time))
            .append("\n")
            .append(versionName).append("(").append(versionCode).append(")")
            .append("\n")
            .append("------------------------- CRASH WRITER TIME -------------------------")
            .append("\n\n")

        val writer = StringWriter()
        val printWriter = PrintWriter(writer)
        e.printStackTrace(printWriter)
        var cause = e.cause
        while (cause != null) {
            cause.printStackTrace(printWriter)
            cause = cause.cause
        }
        printWriter.close()
        val result = writer.toString()
        stringBuilder.append(result)
        // save to local
        val logFilePath = saveLog2File(context, stringBuilder.toString(), prefix)
        // upload log to callback
        crashHandlerListener?.uploadLogCallback(logFilePath, stringBuilder.toString())

        return logFilePath
    }

    /**
     * Save Log to File .
     * @return File Path
     */
    private fun saveLog2File(context: Context, content: String, prefix: String): String {
        val calendar = Calendar.getInstance(Locale.getDefault())
        val dateStr = dateFormat.format(calendar.time)
        val fileName = "$prefix-$dateStr.trace"
        val filePath = getCrashFileDirPath(context) + File.separator + fileName
        val dir = File(filePath)
        if (dir.parentFile?.exists() == false) {
            dir.mkdirs()
        }
        try {
            val fileOutputStream = FileOutputStream(filePath)
            fileOutputStream.write(content.toByteArray())
            fileOutputStream.close()
        } catch (ignore: IOException) {
            return ""
        }
        return dir.absolutePath
    }


}