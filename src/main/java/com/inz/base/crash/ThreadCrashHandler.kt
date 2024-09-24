package com.inz.base.crash

import com.inz.base.util.ZLog

/**
 * Thread Crash handler.
 *
 * -------------------------
 * @author Administrator
 * create Date: 2024/3/1 23:28 by zheng
 */
class ThreadCrashHandler : Thread.UncaughtExceptionHandler {

    companion object {
        private const val TAG = "ThreadCrash"
    }

    override fun uncaughtException(t: Thread, e: Throwable) {
        ZLog.e(TAG, "ThreadCrashHandler-uncaughtException: $t", e)
    }
}