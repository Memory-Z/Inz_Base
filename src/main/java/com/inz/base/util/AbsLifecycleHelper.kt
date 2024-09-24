package com.inz.base.util

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import java.lang.ref.SoftReference

/**
 * Abs Lifecycle Helper.
 *
 * -------------------------
 * @author Administrator
 * create Date: 2024/2/29 22:11 by zheng
 */
abstract class AbsLifecycleHelper<T : LifecycleOwner>(owner: T) : LifecycleEventObserver {

    protected var ownerSoftReference: SoftReference<T>? = null

    init {
        ownerSoftReference = SoftReference(owner)
        try {
            @Suppress("LeakingThis")
            owner.lifecycle.addObserver(this)
        } catch (e: Exception) {
            ThreadPoolTools.runUiThread {
                ownerSoftReference?.get()?.lifecycle?.addObserver(this)
            }
        }
    }


    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_CREATE -> {
                if (needInitBeforeStart()) {
                    val currentState = source.lifecycle.currentState
                    if (currentState.isAtLeast(Lifecycle.State.STARTED)) {
                        throw IllegalStateException("LifecycleOwner $source is attempting to register while current state is $currentState. LifecycleOwners must call register before they are STARTED.")
                    }
                    initActionInOnCreate()
                }
                onCreate()
            }

            Lifecycle.Event.ON_DESTROY -> {
                onBaseDestroy()
            }

            else -> {

            }
        }
    }

    open fun needInitBeforeStart(): Boolean {
        return false
    }

    open fun initActionInOnCreate() {

    }

    open fun onCreate() {

    }

    abstract fun onDestroy()

    private fun onBaseDestroy() {
        onDestroy()
        ownerSoftReference?.get()?.lifecycle?.removeObserver(this)
        ownerSoftReference = null
    }


}