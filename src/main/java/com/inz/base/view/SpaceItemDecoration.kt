package com.inz.base.view

import android.content.Context
import android.graphics.Rect
import android.text.TextUtils
import android.util.TypedValue
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import java.lang.reflect.Type

/**
 *
 *
 * -------------------------
 * @author Administrator
 * create Date: 2023/12/15 23:39 by zheng
 */
class SpaceItemDecoration : RecyclerView.ItemDecoration {

    companion object {
        const val DEFAULT_SPACE_SIZE = 8F
    }

    private var mContext: Context
    private var spaceSize: Float


    constructor(context: Context) : this(context, DEFAULT_SPACE_SIZE)
    constructor(context: Context, spaceSize: Float) : this(
        context,
        spaceSize,
        TypedValue.COMPLEX_UNIT_DIP
    )

    constructor(context: Context, spaceSize: Float, sizeUnit: Int) : super() {
        this.mContext = context
        this.spaceSize = TypedValue.applyDimension(
            sizeUnit, spaceSize, mContext.resources.displayMetrics
        )
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.set(
            outRect.left,
            outRect.top,
            outRect.right,
            outRect.bottom + spaceSize.toInt()
        )
    }
}