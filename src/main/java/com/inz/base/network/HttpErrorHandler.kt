package com.inz.base.network

import io.reactivex.Observable
import io.reactivex.functions.Function

/**
 *
 *
 * -------------------------
 * @author Administrator
 * create Date: 2024/3/10 12:10 by zheng
 */
class HttpErrorHandler<E : Any> : Function<Throwable, Observable<E>> {
    override fun apply(t: Throwable): Observable<E> {
        return Observable.error(t)
    }
}