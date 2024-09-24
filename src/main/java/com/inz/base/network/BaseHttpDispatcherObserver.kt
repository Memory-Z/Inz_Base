package com.inz.base.network

import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.observers.DisposableObserver

/**
 *
 *
 * -------------------------
 * @author Administrator
 * create Date: 2024/3/22 21:25 by zheng
 */
open class BaseHttpDispatcherObserver<T : Any>(private val observer: BaseDispatcherObserver<T>?, val finish: () -> Unit) :
    DisposableObserver<T>() {

    override fun onNext(t: T) {
        observer?.onSuccess(t)
        onFinish()
    }

    override fun onError(e: Throwable) {
        observer?.onFailure(e)
        onFinish()
    }

    override fun onComplete() {
        observer?.onComplete()
    }

    fun onFinish() {
        finish.invoke()
    }
}