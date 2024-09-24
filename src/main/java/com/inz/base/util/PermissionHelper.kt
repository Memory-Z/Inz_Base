package com.inz.base.util

import android.Manifest
import android.app.Activity
import android.app.AlarmManager
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.AlarmManagerCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.FragmentActivity
import java.util.concurrent.LinkedBlockingDeque

/**
 * Permission Helper.
 *
 * -------------------------
 * @author Administrator
 * create Date: 2023/8/27 10:06 by zheng
 */
@Suppress("MemberVisibilityCanBePrivate")
class PermissionHelper private constructor(activity: FragmentActivity) :
    AbsLifecycleHelper<FragmentActivity>(activity) {

    companion object {
        private val helperMap: MutableMap<Activity, PermissionHelper> = mutableMapOf()

        fun getInstance(activity: FragmentActivity): PermissionHelper {
            synchronized(PermissionHelper::class) {
                var permissionHelper = helperMap[activity]
                if (permissionHelper == null) {
                    permissionHelper = PermissionHelper(activity)
                    helperMap[activity] = permissionHelper
                }
                return permissionHelper
            }
        }
    }

    public interface PermissionResultCallback {
        fun onResult(success: Boolean, permissionDeniedArray: Array<String>? = null)

        fun showPermissionRationale(permission: String)
    }

    private data class PermissionRequestInfo(
        val permissions: Array<String>,
        val callback: PermissionResultCallback? = null
    )

    private var multiPermissionLauncher: ActivityResultLauncher<Array<String>>? = null
    private var permissionDeque: LinkedBlockingDeque<PermissionRequestInfo>? = null

    override fun needInitBeforeStart(): Boolean {
        return true
    }

    override fun initActionInOnCreate() {
        super.initActionInOnCreate()
        ownerSoftReference?.get()?.let { activity ->

            multiPermissionLauncher = activity.registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ) { map ->
                val requestInfo = findPermissionInfoByResult(map)
                permissionDeque?.remove(requestInfo)
                requestInfo?.let { info ->
                    val deniedList = mutableListOf<String>()
                    var haveRationale = false
                    map.forEach { (t, u) ->
                        if (!u) {
                            deniedList.add(t)
                            haveRationale = haveRationale.or(showPermissionRationale(t, info))
                        }
                    }
                    if (!haveRationale) {
                        val success = deniedList.size == 0
                        info.callback?.onResult(
                            success,
                            if (success) null else deniedList.toTypedArray()
                        )
                    }
                }
            }

            permissionDeque = LinkedBlockingDeque()

        }
    }

    private fun findPermissionInfoByResult(map: Map<String, Boolean>): PermissionRequestInfo? {
        return permissionDeque?.find { info ->
            var result = false
            if (info.permissions.size == map.size) {
                result = true
                info.permissions.forEach { permission ->
                    result = result.and(map.containsKey(permission))
                }
            }
            result
        }
    }

    override fun onDestroy() {
        multiPermissionLauncher?.unregister()
        multiPermissionLauncher = null

        permissionDeque?.clear()
        permissionDeque = null
    }

    private fun havePermission(permission: String): Boolean {
        return ownerSoftReference?.get()?.let {
            it.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
        } ?: false
    }

    private fun getNoGrantedPermissions(permissions: Array<String>): Array<String>? {
        val deniedPermissionList = mutableListOf<String>()
        permissions.forEach {
            if (!havePermission(it)) {
                deniedPermissionList.add(it)
            }
        }

        return if (deniedPermissionList.size == 0) null else deniedPermissionList.toTypedArray()
    }

    /**
     * show Permission Rationale .
     */
    private fun showPermissionRationale(
        permission: String,
        permissionRequestInfo: PermissionRequestInfo
    ): Boolean {
        return ownerSoftReference?.get()?.let {
            if (ActivityCompat.shouldShowRequestPermissionRationale(it, permission)) {
                permissionRequestInfo.callback?.showPermissionRationale(permission)
                true
            } else false
        } ?: false
    }

    fun requestPermission(
        permissions: Array<String>,
        callback: PermissionResultCallback? = null,
    ) {
        val deniedPermissions = getNoGrantedPermissions(permissions)
        if (deniedPermissions.isNullOrEmpty()) {
            callback?.onResult(true)
        } else {
            synchronized(PermissionHelper::class.java) {
                permissionDeque?.add(PermissionRequestInfo(deniedPermissions, callback))
                multiPermissionLauncher?.launch(deniedPermissions)
            }
        }
    }


    fun requestStoragePermission(
        callback: PermissionResultCallback? = null,
        vararg otherPermission: String
    ) {
        val permissionList = mutableListOf<String>()
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        } else {
            permissionList.add(Manifest.permission.READ_MEDIA_AUDIO)
            permissionList.add(Manifest.permission.READ_MEDIA_IMAGES)
            permissionList.add(Manifest.permission.READ_MEDIA_VIDEO)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                permissionList.add(Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED)
            }
        }
        permissionList.addAll(otherPermission)
        requestPermission(permissionList.toTypedArray(), callback)
    }

    fun haveNotificationPermission(): Boolean {
        return ownerSoftReference?.get()?.let {
            NotificationManagerCompat.from(it).areNotificationsEnabled()
        } ?: false
    }

    fun requestNotificationPermission(callback: PermissionResultCallback? = null) {
        ownerSoftReference?.get()?.let {
            if (haveNotificationPermission()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    val permissionList = arrayOf(Manifest.permission.POST_NOTIFICATIONS)
                    requestPermission(permissionList, callback)
                } else {
                    callback?.onResult(true)
                }
            } else {
                callback?.showPermissionRationale("Notification")
            }
        }
    }

    fun requestExactAlarmPermission(callback: PermissionResultCallback? = null) {
        ownerSoftReference?.get()?.let {
            val alarmManager = it.getSystemService(Context.ALARM_SERVICE) as AlarmManager?
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager?.canScheduleExactAlarms() != true) {
                    callback?.showPermissionRationale(Manifest.permission.SCHEDULE_EXACT_ALARM)
                }
                return
            }
        }
        callback?.onResult(true)
    }


}