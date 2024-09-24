package com.inz.base.view

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.annotation.StyleRes
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.inz.base.R
import com.inz.base.util.ZLog

/**
 * 带标题输框
 *
 * -------------------------
 * @author Administrator
 * create Date: 2023/12/16 14:28 by zheng
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
class TitleQuestionLayout : ConstraintLayout {

    private var mView: View? = null
    private var mContent: Context? = null

    private var titleLl: LinearLayout? = null
    private var titleTv: AppCompatTextView? = null
    private var subTitleTv: AppCompatTextView? = null
    private var titleIconIBtn: AppCompatImageButton? = null

    private var contentLl: LinearLayout? = null
    private var contentViewResId: Int = View.NO_ID

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
                LayoutInflater.from(mContent)
                    .inflate(R.layout.base_title_question_layout, this, true)
        }
        mView?.let {
            titleLl = it.findViewById(R.id.ll_base_title_question_top_content)
            titleTv = it.findViewById(R.id.tv_base_title_question_top_title)
            subTitleTv = it.findViewById(R.id.tv_base_title_question_top_subtitle)
            titleIconIBtn = it.findViewById(R.id.ibtn_base_title_question_top_icon)

            contentLl = it.findViewById(R.id.ll_base_title_question)
        }
    }

    private fun initViewStyle(attrs: AttributeSet) {
        val array = mContent!!.obtainStyledAttributes(attrs, R.styleable.TitleQuestionLayout, 0, 0)
        val titleTextAppearance = array.getResourceId(
            R.styleable.TitleQuestionLayout_tql_title_textAppearance,
            androidx.appcompat.R.style.TextAppearance_AppCompat_Title
        )
        setTitleTextAppearance(titleTextAppearance)


        val subtitleTextAppearance = array.getResourceId(
            R.styleable.TitleQuestionLayout_tql_subtitle_textAppearance,
            androidx.appcompat.R.style.TextAppearance_AppCompat_Subhead
        )
        setSubtitleTextAppearance(subtitleTextAppearance)


        val titleStr = array.getString(R.styleable.TitleQuestionLayout_tql_title)
        setTitle(titleStr)

        val subtitleStr = array.getString(R.styleable.TitleQuestionLayout_tql_subtitle)
        setSubtitle(subtitleStr)


        val titleIconResId =
            array.getResourceId(R.styleable.TitleQuestionLayout_tql_title_icon, R.color.white)
        setTitleIcon(titleIconResId)

        val titleIconTintResId =
            array.getResourceId(
                R.styleable.TitleQuestionLayout_tql_title_icon_tint,
                Resources.ID_NULL
            )
        setTitleIconTint(titleIconTintResId)

        val showSubtitle =
            array.getBoolean(R.styleable.TitleQuestionLayout_tql_show_subtitle, false)
        setSubtitleVisibility(showSubtitle)

        val showTitleIcon =
            array.getBoolean(R.styleable.TitleQuestionLayout_tql_show_title_icon, false)
        setTitleIconVisibility(showTitleIcon)

        val contentMargin =
            array.getDimensionPixelSize(R.styleable.TitleQuestionLayout_tql_content_margin, 8)
        setContentMarginTitle(contentMargin)

        val contentBackgroundRes =
            array.getResourceId(R.styleable.TitleQuestionLayout_tql_content_background, 0)
        setContentBackground(contentBackgroundRes)

        contentViewResId =
            array.getResourceId(R.styleable.TitleQuestionLayout_tql_content_view_id, View.NO_ID)

        array.recycle()
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        if (contentViewResId != View.NO_ID) {
            val view: View = findViewById(contentViewResId)
            removeView(view)
            setContentView(view)
        }
        val count = childCount
        ZLog.i("TAG", "TitleQuestionLayout-onFinishInflate: --->> $count")
        if (count > 1) {
            removeViews(1, count)
        }
    }

    fun setTitleTextAppearance(@StyleRes resId: Int) {
        titleTv?.setTextAppearance(resId)
    }

    fun setSubtitleTextAppearance(@StyleRes resId: Int) {
        subTitleTv?.setTextAppearance(resId)
    }

    fun setSubtitleVisibility(visibility: Boolean) {
        subTitleTv?.visibility = if (visibility) VISIBLE else GONE
    }

    fun setTitleIconVisibility(visibility: Boolean) {
        titleIconIBtn?.visibility = if (visibility) VISIBLE else GONE
    }

    fun setContentMarginTitle(margin: Int) {
        contentLl?.let {
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
        titleIconIBtn?.setImageResource(resId)
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

    fun setIconClickListener(listener: OnClickListener?) {
        titleIconIBtn?.setOnClickListener(listener)
    }

    fun setContentBackground(@DrawableRes resId: Int) {
        contentLl?.setBackgroundResource(resId)
    }

    fun setContentView(view: View?) {
        contentLl?.let {
            it.removeAllViews()
            view?.let { v ->
                if (v.parent != null) {
                    return
                }
                it.addView(v, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT))
            }
        }
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        titleTv?.isEnabled = enabled
        subTitleTv?.isEnabled = enabled
        titleIconIBtn?.isEnabled = enabled
        contentLl?.isEnabled = enabled
    }
}