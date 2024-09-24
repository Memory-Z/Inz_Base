package com.inz.base.base

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.annotation.LayoutRes
import androidx.fragment.app.DialogFragment
import com.inz.base.util.ViewTools

/**
 *
 *
 * -------------------------
 * @author Administrator
 * create Date: 2023/8/26 13:06 by zheng
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
abstract class BaseDialogFragment : DialogFragment(), View.OnClickListener {

    protected var mContext: Context? = null
    protected var mView: View? = null

    abstract fun initWindow()

    @LayoutRes
    abstract fun getLayoutResId(): Int

    abstract fun initView(view: View)

    abstract fun initData()

    abstract fun disposeData(bundle: Bundle?)

    abstract fun updateDialogArguments(window: Window, rect: Rect)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initWindow()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(getLayoutResId(), container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.mContext = context
        this.mView = view
        initView(view)
        initData()
        disposeData(arguments)
    }


    override fun onResume() {
        super.onResume()
        dialog?.window?.let {
            updateDialogArguments(it, ViewTools.getWindowRect(it))
            it.setBackgroundDrawableResource(android.R.color.transparent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        this.mView = null
        this.mContext = null
    }

    override fun onClick(v: View?) {
        if (ViewTools.isFastClick(v)) {
            return
        }
    }
}