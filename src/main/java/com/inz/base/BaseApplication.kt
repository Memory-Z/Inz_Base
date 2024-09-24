package com.inz.base

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.os.Process
import com.inz.base.util.SPHelper

abstract class BaseApplication : Application() {


    abstract fun initNormalConfig()

    abstract fun initOtherProcessConfig()

    abstract fun initMainProcessConfig()

    override fun onCreate() {
        super.onCreate()
        initNormalConfig()
        initBaseConfig()
        if (isMainProcess(this)) {
            initOtherProcessConfig()
            return
        }
        initMainProcessConfig()
    }

    open fun isMainProcess(context: Context): Boolean {
        return context.packageManager.equals(getCurrentProcessName())
    }

    private fun initBaseConfig() {
        SPHelper.init(this)
    }


    open fun getCurrentProcessName(): String {
        val mainPid = Process.myPid()
        val activityManager =
            applicationContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager?
        activityManager?.let { manager ->
            val appProcess = manager.runningAppProcesses
            appProcess.forEach { process ->
                if (process.pid == mainPid) {
                    return process.processName
                }
            }
        }
        return getProcessName()
    }


}