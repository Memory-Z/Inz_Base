package com.inz.base.util

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.FragmentActivity
import java.util.concurrent.LinkedBlockingDeque

/**
 * Activity Launcher Helper
 *
 * -------------------------
 * @author Administrator
 * create Date: 2024/3/1 22:17 by zheng
 */
@Suppress("unused", "MemberVisibilityCanBePrivate", "RedundantVisibilityModifier")
class ActivityLauncherHelper private constructor(owner: FragmentActivity) :
    AbsLifecycleHelper<FragmentActivity>(owner) {

    companion object {
        private val ACTIVITY_HELPER_MAP = mutableMapOf<FragmentActivity, ActivityLauncherHelper>()

        @JvmStatic
        fun getInstance(activity: FragmentActivity): ActivityLauncherHelper {
            synchronized(ActivityLauncherHelper::class.java) {
                var helper = ACTIVITY_HELPER_MAP[activity]
                if (helper == null) {
                    helper = ActivityLauncherHelper(activity)
                    ACTIVITY_HELPER_MAP[activity] = helper
                }
                return helper
            }
        }
    }

    public interface ActivityLauncherResultCallback {
        fun onResult(resultCode: Int, data: Intent? = null)
    }

    private data class ActivityLauncherInfo(
        val intent: Intent,
        val callback: ActivityLauncherResultCallback? = null
    )

    private var activityResultLauncher: ActivityResultLauncher<Intent>? = null
    private var launcherDeque: LinkedBlockingDeque<ActivityLauncherInfo>? = null


    override fun needInitBeforeStart(): Boolean {
        return true
    }

    override fun initActionInOnCreate() {
        super.initActionInOnCreate()
        ownerSoftReference?.get()?.let { activity ->
            launcherDeque = LinkedBlockingDeque()
            activityResultLauncher = activity.registerForActivityResult(
                ActivityResultContracts.StartActivityForResult()
            ) { result ->
                val info = launcherDeque?.pollLast()
                info?.let {
                    it.callback?.onResult(result.resultCode, result.data)
                }
            }
        }
    }

    override fun onCreate() {

    }

    override fun onDestroy() {
        activityResultLauncher?.unregister()
        activityResultLauncher = null

        launcherDeque?.clear()
        launcherDeque = null
    }

    fun startActivityWithResult(intent: Intent, callback: ActivityLauncherResultCallback? = null) {
        startActivityWithResult(intent, options = null, callback)
    }

    fun startActivityWithResult(
        intent: Intent,
        options: ActivityOptionsCompat? = null,
        callback: ActivityLauncherResultCallback? = null
    ) {
        synchronized(ActivityLauncherHelper::class.java) {
            launcherDeque?.addLast(ActivityLauncherInfo(intent, callback))
            activityResultLauncher?.launch(intent, options)
        }
    }

    fun startActivity(intent: Intent) {
        startActivity(intent, null)
    }

    fun startActivity(intent: Intent, options: Bundle? = null) {
        ownerSoftReference?.get()?.startActivity(intent, options)
    }


    fun startActivities(vararg intents: Intent) {
        ownerSoftReference?.get()?.startActivities(intents)
    }

    fun startActivities(vararg intents: Intent, options: Bundle? = null) {
        ownerSoftReference?.get()?.startActivities(intents, options)
    }


    ///////////////////////////////////////////////////////////////////////////
    // Common Intent
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Enter System Settings: Notification
     */
    fun startSettingsNotification(callback: ActivityLauncherResultCallback? = null) {
        ownerSoftReference?.get()?.let {
            startActivityWithResult(IntentConstants.settingsNotificationIntent(it), callback)
        } ?: callback?.onResult(Activity.RESULT_CANCELED, null)
    }

}