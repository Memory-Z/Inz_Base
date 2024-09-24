package com.inz.base.base

import android.view.MenuItem
import androidx.annotation.MenuRes

/**
 *
 *
 * -------------------------
 * @author Administrator
 * create Date: 2023/12/10 22:02 by zheng
 */
interface BaseMenuFmListener {

    fun onActivityOptionsItemSelected(itemMenuItem: MenuItem): Boolean

    @MenuRes
    fun getMenuId(): Int

    fun updateActivityOptionMenu()
}