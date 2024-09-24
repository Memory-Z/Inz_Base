package com.inz.base.network

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 *
 *
 * -------------------------
 * @author Administrator
 * create Date: 2024/3/30 17:34 by zheng
 */
open class BaseHttpModel {

    fun <T : Any> setSubscribe(
        observable: Observable<T>,
        observer: BaseDispatcherObserver<T>?,
        isMain: Boolean
    ) {
        var ioObservable = observable
            .onErrorResumeNext(HttpErrorHandler())
            .subscribeOn(Schedulers.io())

        ioObservable = if (isMain) {
            ioObservable.observeOn(AndroidSchedulers.mainThread())
        } else {
            ioObservable.observeOn(Schedulers.io())
        }

        observer?.let { ioObservable.subscribe(it) }

    }

}