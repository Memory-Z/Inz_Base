package com.inz.base.util

import androidx.lifecycle.LifecycleOwner
import java.util.concurrent.Callable
import java.util.concurrent.Future
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

/**
 * Lifecycle Thread Helper.
 *
 * -------------------------
 * @author Administrator
 * create Date: 2024/3/2 10:06 by zheng
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
class LifecycleThreadHelper private constructor(lifecycleOwner: LifecycleOwner) :
    AbsLifecycleHelper<LifecycleOwner>(lifecycleOwner) {

    companion object {
        private val OWNER_HELPER_MAP = mutableMapOf<LifecycleOwner, LifecycleThreadHelper>()

        @JvmStatic
        fun getInstance(lifecycleOwner: LifecycleOwner): LifecycleThreadHelper {
            synchronized(LifecycleThreadHelper::class.java) {
                var helper = OWNER_HELPER_MAP[lifecycleOwner]
                if (helper == null) {
                    helper = LifecycleThreadHelper(lifecycleOwner)
                    OWNER_HELPER_MAP[lifecycleOwner] = helper
                }
                return helper
            }
        }
    }

    private var futureList: MutableList<Future<*>>? = null

    override fun onDestroy() {
        futureList?.forEach {
            cancelFuture(it)
        }
        futureList?.clear()
        futureList = null
    }

    private fun addFuture(future: Future<*>) {
        synchronized(LifecycleThreadHelper::class.java) {
            futureList?.let {
                if (!it.contains(future)) {
                    it.add(future)
                }
            }
        }
    }

    fun removeFuture(future: Future<*>) {
        synchronized(LifecycleThreadHelper::class.java) {
            cancelFuture(future)
            futureList?.remove(future)
        }
    }

    fun cancelFuture(future: Future<*>) {
        if (!future.isDone && !future.isCancelled) {
            future.cancel(true)
        }
    }

    fun submitTaskToSingle(runnable: Runnable): Future<*> =
        ThreadPoolTools.singleExecutor.submit(runnable).apply {
            addFuture(this)
        }

    fun <T> submitTaskToSingle(runnable: Runnable, result: T): Future<T> =
        ThreadPoolTools.singleExecutor.submit(runnable, result).apply {
            addFuture(this)
        }

    fun <T> submitTaskToSingle(callable: Callable<T>): Future<T> =
        ThreadPoolTools.singleExecutor.submit(callable).apply {
            addFuture(this)
        }

    fun submitTaskToPool(runnable: Runnable): Future<*> =
        ThreadPoolTools.workExecutor.submit(runnable).apply {
            addFuture(this)
        }

    fun <T> submitTaskToPool(runnable: Runnable, result: T): Future<T> =
        ThreadPoolTools.workExecutor.submit(runnable, result).apply {
            addFuture(this)
        }

    fun <T> submitTaskToPool(callable: Callable<T>): Future<T> =
        ThreadPoolTools.workExecutor.submit(callable).apply {
            addFuture(this)
        }


    fun submitTaskToSchedule(runnable: Runnable): Future<*> =
        ThreadPoolTools.scheduledExecutor.submit(runnable).apply {
            addFuture(this)
        }

    fun <T> submitTaskToSchedule(runnable: Runnable, result: T): Future<T> =
        ThreadPoolTools.scheduledExecutor.submit(runnable, result).apply {
            addFuture(this)
        }

    fun <T> submitTaskToSchedule(callable: Callable<T>): Future<T> =
        ThreadPoolTools.scheduledExecutor.submit(callable).apply {
            addFuture(this)
        }

    fun scheduleTaskToSchedule(
        runnable: Runnable,
        delay: Long,
        timeUnit: TimeUnit
    ): ScheduledFuture<*> =
        ThreadPoolTools.scheduledExecutor.schedule(runnable, delay, timeUnit).apply {
            addFuture(this)
        }

    fun <T> scheduleTaskToSchedule(
        callable: Callable<T>,
        delay: Long,
        timeUnit: TimeUnit
    ): ScheduledFuture<T> =
        ThreadPoolTools.scheduledExecutor.schedule(callable, delay, timeUnit).apply {
            addFuture(this)
        }

    fun scheduleAtFixedRate(
        runnable: Runnable,
        initialDelay: Long,
        delay: Long,
        timeUnit: TimeUnit
    ): ScheduledFuture<*> =
        ThreadPoolTools.scheduledExecutor.scheduleAtFixedRate(
            runnable,
            initialDelay,
            delay,
            timeUnit
        ).apply {
            addFuture(this)
        }


    fun scheduleWithFixedDelay(
        runnable: Runnable,
        initialDelay: Long,
        delay: Long,
        timeUnit: TimeUnit
    ): ScheduledFuture<*> =
        ThreadPoolTools.scheduledExecutor.scheduleWithFixedDelay(
            runnable,
            initialDelay,
            delay,
            timeUnit
        ).apply {
            addFuture(this)
        }
}