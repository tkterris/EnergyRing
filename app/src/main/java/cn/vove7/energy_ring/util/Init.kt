package cn.vove7.energy_ring.util

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.util.Log
import cn.vove7.energy_ring.BuildConfig
import cn.vove7.energy_ring.service.ForegroundService
import cn.vove7.smartkey.android.AndroidSettings
import cn.vove7.smartkey.get
import kotlin.concurrent.thread

/**
 * # Init
 *
 * @author tkterris
 * 2021-11-13
 */

fun configAndStartForeground (context: Context) {

    AndroidSettings.init(context)
    if ("app_version_code" in Config) {
        val lastVersion = Config["app_version_code", 0]
        if (BuildConfig.VERSION_CODE > lastVersion) {
            Config["app_version_code"] = BuildConfig.VERSION_CODE
        }
    } else {
        Config["app_version_code"] = BuildConfig.VERSION_CODE
    }

    val foreService = Intent(context, ForegroundService::class.java)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        context.startForegroundService(foreService)
    } else {
        context.startService(foreService)
    }
}

