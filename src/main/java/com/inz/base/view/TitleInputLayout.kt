package com.inz.base.view

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.annotation.StyleRes
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.inz.base.R

/**
 * 带标题输框
 *
 * -------------------------
 * @author Administrator
 * create Date: 2023/12/16 14:28 by zheng
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
class TitleInputLayout : ConstraintLayout {

    private var mView: View? = null
    private var mContent: Context? = null

    private var titleLl: LinearLayout? = null
    private var titleTv: AppCompatTextView? = null
    private var subTitleTv: AppCompatTextView? = null
    private var titleIconIBtn: AppCompatImageButton? = null

    private var contextEt: AppCompatEditText? = null


    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        this.mContent = context
        initView()
        attrs?.let {
            initViewStyle(it)
        }
    }

    private fun initView() {
        if (mView == null) {
            mView =
                LayoutInflater.from(mContent).inflate(R.layout.base_title_input_layout, this, true)
        }
        mView?.let {
            titleLl = it.findViewById(R.id.ll_base_title_input_top_content)
            titleTv = it.findViewById(R.id.tv_base_title_input_top_title)
            subTitleTv = it.findViewById(R.id.tv_base_title_input_top_subtitle)
            titleIconIBtn = it.findViewById(R.id.ibtn_base_title_input_top_icon)

            contextEt = it.findViewById(R.id.et_base_title_input)
        }
    }

    private fun initViewStyle(attrs: AttributeSet) {
        val array = mContent!!.obtainStyledAttributes(attrs, R.styleable.TitleInputLayout, 0, 0)
        val titleTextAppearance = array.getResourceId(
            R.styleable.TitleInputLayout_til_title_textAppearance,
            androidx.appcompat.R.style.TextAppearance_AppCompat_Title
        )
        setTitleTextAppearance(titleTextAppearance)


        val subtitleTextAppearance = array.getResourceId(
            R.styleable.TitleInputLayout_til_subtitle_textAppearance,
            androidx.appcompat.R.style.TextAppearance_AppCompat_Subhead
        )
        setSubtitleTextAppearance(subtitleTextAppearance)

        val editTextAppearance = array.getResourceId(
            R.styleable.TitleInputLayout_til_edit_textAppearance,
            androidx.appcompat.R.style.TextAppearance_AppCompat_Body1
        )
        setEditTextAppearance(editTextAppearance)

        val titleStr = array.getString(R.styleable.TitleInputLayout_til_title)
        setTitle(titleStr)

        val subtitleStr = array.getString(R.styleable.TitleInputLayout_til_subtitle)
        setSubtitle(subtitleStr)

        val contentStr = array.getString(R.styleable.TitleInputLayout_til_edit_content)
        setEditContent(contentStr)

        val titleIconResId =
            array.getResourceId(R.styleable.TitleInputLayout_til_title_icon, R.color.white)
        setTitleIcon(titleIconResId)

        val titleIconTintResId =
            array.getResourceId(R.styleable.TitleInputLayout_til_title_icon_tint, Resources.ID_NULL)
        setTitleIconTint(titleIconTintResId)

        val showSubtitle = array.getBoolean(R.styleable.TitleInputLayout_til_show_subtitle, false)
        setSubtitleVisibility(showSubtitle)

        val showTitleIcon =
            array.getBoolean(R.styleable.TitleInputLayout_til_show_title_icon, false)
        setTitleIconVisibility(showTitleIcon)

        val editMargin =
            array.getDimensionPixelSize(R.styleable.TitleInputLayout_til_edit_margin, 8)
        setEditMarginTitle(editMargin)

        val editBackgroundRes =
            array.getResourceId(R.styleable.TitleInputLayout_til_edit_background, 0)
        setEditBackground(editBackgroundRes)

        array.recycle()
    }

    fun setTitleTextAppearance(@StyleRes resId: Int) {
        titleTv?.setTextAppearance(resId)
    }

    fun setSubtitleTextAppearance(@StyleRes resId: Int) {
        subTitleTv?.setTextAppearance(resId)
    }

    fun setEditTextAppearance(@StyleRes resId: Int) {
        contextEt?.setTextAppearance(resId)
    }

    fun setSubtitleVisibility(visibility: Boolean) {
        subTitleTv?.visibility = if (visibility) VISIBLE else GONE
    }

    fun setTitleIconVisibility(visibility: Boolean) {
        titleIconIBtn?.visibility = if (visibility) VISIBLE else GONE
    }

    fun setEditMarginTitle(margin: Int) {
        contextEt?.let {
            val lp = it.layoutParams as LayoutParams
            lp.setMargins(lp.leftMargin, margin, lp.rightMargin, lp.bottomMargin)
            it.layoutParams = lp
        }
    }

    fun setTitle(@StringRes resId: Int) {
        titleTv?.let {
            it.text = it.context.getString(resId)
        }
    }

    fun setTitle(value: String?) {
        titleTv?.text = value
    }

    fun setSubtitle(@StringRes resId: Int) {
        subTitleTv?.let {
            it.text = it.context.getString(resId)
        }
    }

    fun setSubtitle(value: String?) {
        subTitleTv?.text = value
    }

    fun setTitleIcon(@DrawableRes resId: Int) {
        titleIconIBtn?.let {
            val drawable = ContextCompat.getDrawable(it.context, resId)
            setTitleIcon(drawable)
        }
    }

    fun setTitleIcon(drawable: Drawable?) {
        titleIconIBtn?.setImageDrawable(drawable)
    }

    fun setTitleIconTint(@ColorRes colorRes: Int) {
        if (colorRes != Resources.ID_NULL) {
            titleIconIBtn?.let {
                val csl = ContextCompat.getColorStateList(it.context, colorRes)
                it.imageTintList = csl
            }
        }
    }


    fun setTitleIconTint(csl: ColorStateList) {
        titleIconIBtn?.imageTintList = csl
    }


    fun setEditContent(value: String?) {
        contextEt?.setText(value)
    }

    fun setIconClickListener(listener: OnClickListener?) {
        titleIconIBtn?.setOnClickListener(listener)
    }

    fun getEditContent(): String? {
        return contextEt?.text?.toString()
    }

    fun setEditBackground(@DrawableRes resId: Int) {
        contextEt?.setBackgroundResource(resId)
    }

}