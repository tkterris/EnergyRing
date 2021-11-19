package cn.vove7.energy_ring.util

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import cn.vove7.energy_ring.App
import cn.vove7.energy_ring.BuildConfig
import cn.vove7.energy_ring.listener.PowerEventReceiver
import cn.vove7.energy_ring.listener.RotationListener
import cn.vove7.energy_ring.listener.ScreenListener
import cn.vove7.energy_ring.service.AccService
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

fun configAndStartForeground () {

    AndroidSettings.init(App.INS)
    if ("app_version_code" in Config) {
        val lastVersion = Config["app_version_code", 0]
        if (BuildConfig.VERSION_CODE > lastVersion) {
            onNewVersion(lastVersion, BuildConfig.VERSION_CODE, context)
            Config["app_version_code"] = BuildConfig.VERSION_CODE
        }
    } else {
        Config["app_version_code"] = BuildConfig.VERSION_CODE
        onFirstLaunch(context)
    }

    ScreenListener.start()
    PowerEventReceiver.start()
    RotationListener.start()

    if (!accServiceEnabled) {
        startEnergyForegroundService()
    }
}

fun startEnergyForegroundService () {
    val foreService = Intent(App.INS, ForegroundService::class.java)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        App.INS.startForegroundService(foreService)
    } else {
        App.INS.startService(foreService)
    }
}

fun stopEnergyForegroundService () {
    if (ForegroundService.running) {
        val foreService = Intent(App.INS, ForegroundService::class.java)
        App.INS.stopService(foreService)
    }
}

private fun onFirstLaunch(context: Context) = thread {
    initSmsApp2NotifyApps(context)
    initPhone2NotifyApps(context)
}

private fun initPhone2NotifyApps(context: Context) {
    val i = Intent(Intent.ACTION_CALL, Uri.parse("tel:123"))
    val ri = context.packageManager.resolveActivity(i, PackageManager.MATCH_DEFAULT_ONLY)

    Log.d("Debug :", "sms  ----> $ri")
    ri?.activityInfo?.packageName?.also { smsPkg ->
        Log.d("Debug :", "拨号应用  ----> $smsPkg")
        Config.notifyApps.add(smsPkg)
    }
}

private fun initSmsApp2NotifyApps(context: Context) {
    val i = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:123"))
    val ri = context.packageManager.resolveActivity(i, PackageManager.MATCH_DEFAULT_ONLY)

    Log.d("Debug :", "sms  ----> $ri")
    ri?.activityInfo?.packageName?.also { smsPkg ->
        Log.d("Debug :", "短信应用  ----> $smsPkg")
        Config.notifyApps.add(smsPkg)
    }
}

private fun onNewVersion(lastVersion: Int, newVersion: Int, context: Context) {
    if (lastVersion <= 20401 && newVersion >= 20402) {
        thread { initPhone2NotifyApps(context) }
    }
}
