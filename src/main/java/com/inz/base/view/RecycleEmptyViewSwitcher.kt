@file:Suppress("MemberVisibilityCanBePrivate")

package com.inz.base.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.ViewSwitcher
import androidx.recyclerview.widget.RecyclerView
import com.inz.base.R

/**
 * Recycle View / Empty View
 *
 * -------------------------
 * @author Administrator
 * create Date: 2023/12/16 0:01 by zheng
 */
class RecycleEmptyViewSwitcher : LinearLayout {

    companion object {
        private const val TAG = "RecycleEmptyViewSwitche"
    }

    private var mView: View? = null
    private var mContext: Context? = null

    private lateinit var emptyView: LinearLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewSwitcher: ViewSwitcher

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        this.mContext = context
        initView()
    }


    private fun initView() {
        if (mView == null) {
            mView = LayoutInflater.from(mContext!!)
                .inflate(R.layout.base_recycle_empty_view, this, true)
        }
        mView?.let {
            viewSwitcher = it.findViewById(R.id.view_switch_rev_root)
            emptyView = it.findViewById(R.id.ll_base_rev_empty)
            recyclerView = it.findViewById(R.id.rv_base_rev_content)
        }
    }

    fun getEmptyContent(): LinearLayout = emptyView

    fun getRecycleView(): RecyclerView = recyclerView


    fun setEmptyView(view: View) {
        emptyView.let {
            it.removeAllViews()
            it.addView(view)
        }
    }

    fun showEmptyView() {
        if (!currentShowEmpty()) {
            viewSwitcher.showPrevious()
        }
    }

    fun showContentView() {
        if (currentShowEmpty()) {
            viewSwitcher.showNext()
        }
    }

    fun currentShowEmpty(): Boolean {
        return viewSwitcher.displayedChild == 0
    }
}