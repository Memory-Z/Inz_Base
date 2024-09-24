package com.inz.base.base

import android.view.MenuItem
import android.view.View

/**
 * Base Menu Fragment .
 *
 * -------------------------
 * @author Administrator
 * create Date: 2023/12/10 22:14 by zheng
 */
abstract class BaseMenuFmFragment : BaseFragment(), BaseMenuFmListener {

    override fun onActivityOptionsItemSelected(itemMenuItem: MenuItem): Boolean {
        return false
    }

    override fun getMenuId(): Int {
        return View.NO_ID
    }

    override fun updateActivityOptionMenu() {
        activity?.invalidateOptionsMenu()
    }
}