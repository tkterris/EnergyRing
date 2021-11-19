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
            Config["app_version_code"] = BuildConfig.VERSION_CODE
        }
    } else {
        Config["app_version_code"] = BuildConfig.VERSION_CODE
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

