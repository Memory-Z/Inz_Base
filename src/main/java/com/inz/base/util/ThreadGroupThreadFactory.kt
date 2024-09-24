package com.inz.base.util

import com.inz.base.crash.ThreadCrashHandler
import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicInteger

/**
 * Thread Group Thread Factory.
 *
 * -------------------------
 * @author Administrator
 * create Date: 2023/9/16 0:20 by zheng
 */
class ThreadGroupThreadFactory(prefix: String) : ThreadFactory {
    companion object {
        private val groupNumber = AtomicInteger(1)
        val threadNumber = AtomicInteger(1)

    }

    var threadGroup: ThreadGroup
    var namePrefix: String = ""

    init {
        val manager = System.getSecurityManager()
        threadGroup = manager?.threadGroup ?: Thread.currentThread().threadGroup!!
        namePrefix = "$prefix-${groupNumber.getAndIncrement()}-THREAD-"
    }

    override fun newThread(r: Runnable?): Thread {
        return Thread(
            threadGroup,
            r,
            "$namePrefix${threadNumber.getAndIncrement()}",
            0
        ).apply {
            this.uncaughtExceptionHandler = ThreadCrashHandler()
            isDaemon = false
            priority = Thread.NORM_PRIORITY
        }
    }
}