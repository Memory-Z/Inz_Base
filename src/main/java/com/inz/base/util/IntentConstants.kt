package com.inz.base.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi

/**
 * Common Intent Constants
 *
 * -------------------------
 * @author Administrator
 * create Date: 2024/3/2 9:38 by zheng
 */
@Suppress("unused")
object IntentConstants {

    /**
     * Settings: Exact Alarm Page.
     */
    @RequiresApi(Build.VERSION_CODES.S)
    fun settingsExactAlarmIntent(context: Context): Intent {
        val uri = Uri.parse("package:${context.packageName}")
        return Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM, uri)
    }

    /**
     * Settings: Notification Page
     */
    fun settingsNotificationIntent(context: Context): Intent {
        val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
        intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
        intent.putExtra(Settings.EXTRA_CHANNEL_ID, context.applicationInfo.uid)
        return intent
    }

    /**
     * Settings: Application Info Page
     */
    fun settingsApplicationInfoIntent(context: Context): Intent {
        val uri = Uri.parse("package:${context.packageName}")
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.setData(uri)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        return intent
    }
}