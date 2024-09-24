package com.inz.base.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import com.inz.base.util.ViewTools

/**
 * Base Fragment
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
abstract class BaseFragment : Fragment(), View.OnClickListener {

    protected var mContext: Context? = null
    protected var mView: View? = null


    @LayoutRes
    abstract fun getLayoutResId(): Int

    abstract fun initView(rootView: View)

    abstract fun initData()

    abstract fun disposeData(bundle: Bundle?)

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