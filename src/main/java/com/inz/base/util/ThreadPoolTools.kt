package com.inz.base.util

import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

/**
 * Thread Pool Tools.
 *
 * -------------------------
 * @author Administrator
 * create Date: 2023/8/26 19:00 by zheng
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
object ThreadPoolTools {

    private val CORE_POOL_SIZE = 2 * Runtime.getRuntime().availableProcessors() + 1
    private val MAX_POOL_SIZE = 2 * CORE_POOL_SIZE
    private const val KEEP_ALIVE_TIME = 5L

    private val uiExecutors: MainThreadExecutor
    val workExecutor: ThreadPoolExecutor
    val scheduledExecutor: ScheduledExecutorService
    val singleExecutor: ExecutorService

    init {
        uiExecutors = MainThreadExecutor()
        workExecutor = ThreadPoolExecutor(
            CORE_POOL_SIZE,
            MAX_POOL_SIZE,
            KEEP_ALIVE_TIME,
            TimeUnit.MINUTES,
            LinkedBlockingDeque(),
            ThreadGroupThreadFactory("WORKER")
        )

        scheduledExecutor = Executors.newScheduledThreadPool(
            CORE_POOL_SIZE,
            ThreadGroupThreadFactory("SCHEDULED")
        )
        singleExecutor = Executors.newSingleThreadExecutor(
            ThreadGroupThreadFactory("SINGLE")
        )

    }


    class MainThreadExecutor : Executor {
        private val mainHandler = Handler(Looper.getMainLooper())

        override fun execute(command: Runnable?) {
            command?.let {
                mainHandler.post(it)
            }
        }

        fun executeDelayed(command: Runnable?, delay: Long) {
            command?.let {
                mainHandler.postDelayed(it, delay)
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // OPEN
    ///////////////////////////////////////////////////////////////////////////

    fun runUiThread(r: Runnable) {
        uiExecutors.execute(r)
    }

    fun runUiThreadDelayed(r: Runnable, delayed: Long) {
        uiExecutors.executeDelayed(r, delayed)
    }
}