package com.inz.base.base

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.activity.OnBackPressedCallback
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.inz.base.R
import com.inz.base.util.PermissionHelper

/**
 *
 *
 * ----------------------------------
 * @author Zheng Lunjie
 * create Date: 2023/8/17 15:09 by zheng.
 */
@Suppress("unused", "MemberVisibilityCanBePrivate", "DEPRECATION")
abstract class BaseActivity : AppCompatActivity() {

    protected var mContext: Context? = null
    private var mAppBarLayout: AppBarLayout? = null
    private var mCollapsingToolbarLayout: CollapsingToolbarLayout? = null
    private var mToolbar: Toolbar? = null
    private var mRootView: ViewGroup? = null
    private var mMainContentLl: LinearLayout? = null
    private var mRootContentCl: ConstraintLayout? = null
    private var coordinatorViewList: MutableList<View>? = null
    private var fullScreenView: View? = null

    @LayoutRes
    open fun getLayoutId(): Int = -1

    abstract fun initView(rootView: View)

    abstract fun initData()

    abstract fun disposeData(bundle: Bundle?)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContext = this
        setFullScreenOption()
        this.mRootView = initBaseRootView()
        setContentView(mRootView)
        setMainContentView(getLayoutId())
        initToolbarStatus()
        PermissionHelper.getInstance(this)
        initBaseData()
        initView(mMainContentLl!!)
        initData()
        disposeData(intent.extras)
        onBackPressedDispatcher.addCallback(
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (!this@BaseActivity.handleOnBackPressed()) {
                        finish()
                    }
                }
            }
        )
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        disposeData(intent?.extras)
    }

    private fun initBaseData() {
        coordinatorViewList = mutableListOf()
    }

    @SuppressLint("InflateParams")
    private fun initBaseRootView(): ViewGroup {
        val rootView = if (scrollableToolBar()) {
            layoutInflater.inflate(R.layout.activity_base_scroll_content, null) as ViewGroup
        } else {
            layoutInflater.inflate(R.layout.activity_base_content, null) as ViewGroup
        }
        mRootContentCl = rootView.findViewById(R.id.cl_base_activity_root_content)
        mMainContentLl = rootView.findViewById(R.id.ll_base_activity_main_content)
        mAppBarLayout = rootView.findViewById(R.id.abl_base_activity)
        mCollapsingToolbarLayout = rootView.findViewById(R.id.ctl_base_activity)
        mToolbar = rootView.findViewById(R.id.toolbar_base_activity)
        var title: String? = null
        getTitleStrId()?.let {
            title = getString(it)
        }
        mToolbar?.title = title ?: getTitleStr()
        return rootView
    }

    private fun setMainContentView(@LayoutRes layoutId: Int) {
        if (layoutId != -1) {
            setMainContentView(layoutInflater.inflate(layoutId, null))
        }
    }

    private fun setMainContentView(view: View) {
        mMainContentLl?.let {
            it.removeAllViews()
            it.addView(
                view,
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
                )
            )
        }
    }

    private fun initToolbarStatus() {
        val haveToolbar = haveTopToolbar()
        mAppBarLayout?.visibility = if (haveToolbar) View.VISIBLE else View.GONE
        if (haveToolbar) {
            setAppBarLayoutConfig(mAppBarLayout)
            setCollapsingToolbarLayoutConfig(mCollapsingToolbarLayout)
            updateToolbarConfig()
        }
    }

    fun updateToolbarConfig() {
        setToolbarConfig(mToolbar)
    }

    /**
     * Add Coordinator Child View.
     */
    fun addCoordinatorLayoutView(
        view: View,
        layoutParams: CoordinatorLayout.LayoutParams = CoordinatorLayout.LayoutParams(
            CoordinatorLayout.LayoutParams.MATCH_PARENT,
            CoordinatorLayout.LayoutParams.WRAP_CONTENT
        )
    ) {
        mRootContentCl?.let {
            it.addView(view, layoutParams)
            coordinatorViewList?.add(view)
        }
    }

    /**
     * remove Coordinator Child View.
     */
    fun removeCoordinatorLayoutView(view: View) {
        mRootContentCl?.let {
            it.removeView(view)
            coordinatorViewList?.remove(view)
        }
    }

    fun showFullScreenView(view: View) {
        mRootView?.let { viewGroup ->
            fullScreenView?.let {
                viewGroup.removeView(it)
            }
            viewGroup.addView(
                view,
                ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            )
            fullScreenView = view
            updateWindowStatus(viewGroup.context)
        }
    }

    fun hideFullScreenView() {
        mRootView?.let { viewGroup ->
            fullScreenView?.let {
                viewGroup.removeView(it)
            }
            fullScreenView = null
            updateWindowStatus(viewGroup.context)
        }
    }

    private fun setFullScreenOption() {
//        WindowCompat.setDecorFitsSystemWindows(window, false)
        val decorView = window.decorView
        val isDarkMode = isDarkMode(decorView.context)
        updateWindowStatusTextColor(isDarkMode)
        updateWindowStatus(decorView.context)
    }

    protected fun updateWindowStatus(context: Context) {
        if (currentHaveCustomTheme()) {
            return
        }
        val navColor: Int
        val statusColor: Int

        val transColor = ContextCompat.getColor(context, android.R.color.transparent)
        if (fullScreenView != null) {
            navColor = transColor
            statusColor = transColor
        } else {
            navColor = navigationBarColor()
            statusColor = if (haveTopToolbar()) {
                statusBarColor()
            } else {
                transColor
            }
        }

        window.navigationBarColor = navColor
        window.statusBarColor = statusColor
    }

    protected fun updateWindowStatusTextColor(isDark: Boolean) {
        val decorView = window.decorView
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val showDark = if (statusTextColorChangeByMode()) isDark else true
            decorView.windowInsetsController?.setSystemBarsAppearance(
                if (showDark) 0 else WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
        } else {
            decorView.systemUiVisibility =
                if (isDark) View.SYSTEM_UI_FLAG_VISIBLE else View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    }

    protected fun isDarkMode(context: Context): Boolean {
        return context.resources.configuration.uiMode.or(Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
    }

    open fun statusBarColor(): Int {
        val transColor = Color.TRANSPARENT
        val typedArray = theme.obtainStyledAttributes(
            intArrayOf(androidx.appcompat.R.attr.colorPrimary)
        )
        val color = typedArray.getColor(0, transColor)
        typedArray.recycle()
        return color
    }

    open fun navigationBarColor(): Int {
        val isDarkMode = isDarkMode(this)
        val navColorId = if (isDarkMode) {
            android.R.color.background_dark
        } else {
            android.R.color.background_light
        }
        return ContextCompat.getColor(this, navColorId)
    }

    open fun scrollableToolBar(): Boolean {
        return false
    }

    open fun currentHaveCustomTheme(): Boolean {
        return false
    }

    open fun statusTextColorChangeByMode(): Boolean {
        return true
    }

    open fun haveTopToolbar(): Boolean {
        return true
    }

    open fun setAppBarLayoutConfig(appBarLayout: AppBarLayout?) {

    }

    open fun setCollapsingToolbarLayoutConfig(collapsingToolbarLayout: CollapsingToolbarLayout?) {

    }

    open fun setToolbarConfig(toolbar: Toolbar?) {
        toolbar?.let {
            setSupportActionBar(it)
        }
    }

    open fun getTitleStr(): String {
        return getString(R.string.app_name)
    }

    @StringRes
    open fun getTitleStrId(): Int? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        destroyBaseData()
        mToolbar = null
        mMainContentLl = null
        mRootContentCl = null
        mRootView = null
        mContext = null
    }

    private fun destroyBaseData() {
        coordinatorViewList?.clear()
        coordinatorViewList = null
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            if (handleOnBackPressed()) {
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    @Suppress("OVERRIDE_DEPRECATION")
    override fun onBackPressed() {
        if (handleOnBackPressed()) {
            return
        }
        super.onBackPressed()
    }

    open fun handleOnBackPressed(): Boolean {
        return false
    }

    @IdRes
    open fun getFragmentContentId(): Int {
        return R.id.ll_base_activity_main_content
    }

    protected fun <T : Fragment> addAndShowFragment(
        newFragment: T?,
        tag: String,
        addStack: Boolean = true,
        alwaysShowNew: Boolean = false
    ) {
        val manager = supportFragmentManager
        val fragment = if (alwaysShowNew) {
            newFragment
        } else {
            var oldFragment = manager.findFragmentByTag(tag)
            if (oldFragment == null) {
                oldFragment = newFragment
            }
            oldFragment
        }
        fragment?.let {
            val transaction = manager.beginTransaction()
            transaction.add(getFragmentContentId(), it, tag)
            if (addStack) {
                transaction.addToBackStack(tag)
            }
            transaction.commitAllowingStateLoss()
        }

    }
}