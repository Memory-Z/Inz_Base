package com.inz.base.base

import android.view.View

/**
 *
 *
 * -------------------------
 * @author Administrator
 * create Date: 2023/12/15 21:54 by zheng
 */
abstract class BaseItemView<T> {

    abstract fun bind(data: T)

    abstract fun getRootView(): View
}