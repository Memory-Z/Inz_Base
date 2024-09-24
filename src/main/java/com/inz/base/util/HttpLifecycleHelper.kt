package com.inz.base.util

import androidx.lifecycle.LifecycleOwner
import com.inz.base.network.BaseDispatcherObserver
import com.inz.base.network.BaseHttpDispatcherObserver
import com.inz.base.network.HttpErrorHandler
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.LinkedBlockingQueue

/**
 *
 *
 * -------------------------
 * @author Administrator
 * create Date: 2024/3/10 12:12 by zheng
 */
class HttpLifecycleHelper private constructor(owner: LifecycleOwner) :
    AbsLifecycleHelper<LifecycleOwner>(owner) {

    companion object {
        private val HTTP_LIFECYCLE_MAP = mutableMapOf<LifecycleOwner, HttpLifecycleHelper>()

        @JvmStatic
        fun getInstance(owner: LifecycleOwner): HttpLifecycleHelper {
            synchronized(HttpLifecycleHelper::class.java) {
                var helper = HTTP_LIFECYCLE_MAP[owner]
                if (helper == null) {
                    helper = HttpLifecycleHelper(owner)
                    HTTP_LIFECYCLE_MAP[owner] = helper
                }
                return helper
            }
        }
    }

    private var disposableList: LinkedBlockingQueue<Disposable>? = null

    override fun initActionInOnCreate() {
        super.initActionInOnCreate()
        disposableList = LinkedBlockingQueue()
    }

    override fun onDestroy() {

    }

    private fun addDisposable(disposable: Disposable) {
        synchronized(HttpLifecycleHelper::class.java) {
            if (disposableList?.contains(disposable) == false) {
                disposableList?.add(disposable)
            }
        }
    }

    private fun removeDisposable(disposable: Disposable) {
        synchronized(HttpLifecycleHelper::class.java) {
            disposableList?.remove(disposable)
        }

    }


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

        val disposable = ioObservable
            .subscribeWith(BaseHttpDispatcherObserver(observer) {
                observer?.let {
                    removeDisposable(it)
                }
            })

        addDisposable(disposable)
    }
}