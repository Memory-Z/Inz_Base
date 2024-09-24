package com.inz.base.util

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.os.Build
import android.util.TypedValue
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.annotation.StringRes

/**
 * View Tools.
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
object ViewTools {

    fun showShortToast(context: Context, @StringRes stringResId: Int) {
        showShortToast(context, context.getString(stringResId))
    }

    fun showShortToast(context: Context, content: String) {
        Toast.makeText(context, content, Toast.LENGTH_SHORT).show()
    }

    fun showLongToast(context: Context, @StringRes stringResId: Int) {
        showLongToast(context, context.getString(stringResId))
    }

    fun showLongToast(context: Context, content: String) {
        Toast.makeText(context, content, Toast.LENGTH_LONG).show()
    }

    fun dp2px(context: Context, dpValue: Float): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_PX,
            dpValue,
            context.resources.displayMetrics
        )
    }

    fun px2Dp(context: Context, pxValue: Float): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            pxValue,
            context.resources.displayMetrics
        )
    }

    ///////////////////////////////////////////////////////////////////////////
    // Fast Click
    ///////////////////////////////////////////////////////////////////////////
    private val clickViewMap: MutableMap<Int, Long> = mutableMapOf()
    private const val DEFAULT_FAST_CLICK = 1000L

    /**
     * Check current View is Fast Click
     */
    fun isFastClick(view: View?, duration: Long = DEFAULT_FAST_CLICK): Boolean {
        if (view == null) {
            return false
        }
        val viewId = view.id
        val currentTime = System.currentTimeMillis()
        if (clickViewMap.containsKey(viewId)) {
            val oldTime = clickViewMap[viewId] ?: 0
            if (currentTime - oldTime < duration) {
                return true
            }
        }
        clickViewMap[viewId] = currentTime
        return false
    }

    /**
     * Get Window Rect.
     */
    @Suppress("DEPRECATION")
    fun getWindowRect(window: Window): Rect {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.windowManager.currentWindowMetrics.bounds
        } else {
            val rect = Rect()
            window.windowManager.defaultDisplay.getRectSize(rect)
            rect
        }
    }

    @SuppressLint("InternalInsetResource", "DiscouragedApi")
    fun getStatusBarHeight(context: Context): Int {
        var result = 0
        val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = context.resources.getDimensionPixelSize(resourceId)
        }
        return result
    }
}