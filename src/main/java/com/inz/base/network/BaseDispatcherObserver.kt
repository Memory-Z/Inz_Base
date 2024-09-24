package com.inz.base.network

import io.reactivex.observers.DisposableObserver

/**
 * Base Disposable Observer
 *
 * -------------------------
 * @author Administrator
 * create Date: 2024/3/10 12:05 by zheng
 */
@Suppress("MemberVisibilityCanBePrivate")
abstract class BaseDispatcherObserver<T : Any> : DisposableObserver<T>() {

    override fun onNext(value: T) {
        onSuccess(value)
    }

    override fun onError(e: Throwable) {
        onFailure(e)
        onFinish()
    }

    override fun onComplete() {
        onFinish()
    }

    fun onFinish() {

    }

    abstract fun onSuccess(data: T)
    abstract fun onFailure(e: Throwable)
}