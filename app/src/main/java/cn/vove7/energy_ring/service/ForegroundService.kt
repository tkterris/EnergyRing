package cn.vove7.energy_ring.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import cn.vove7.energy_ring.floatwindow.FloatRingWindow
import cn.vove7.energy_ring.util.FOREGROUND_NOTIFICATION_ID
import cn.vove7.energy_ring.util.getAndShowServiceDestroyedNotification
import cn.vove7.energy_ring.util.getAndShowForeNotification

/**
 * # ForegroundService
 *
 * @author Vove
 * 2020/5/13
 */
class ForegroundService : Service() {

    companion object {
        var running : Boolean = false
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("ForegroundService", "starting service")
        running = true

        startForeground(FOREGROUND_NOTIFICATION_ID, getAndShowForeNotification(this))
        FloatRingWindow.checkPermissionAndUpdate()

        return START_STICKY
    }

    override fun onDestroy() {
        Log.d("ForegroundService", "stopping service")
        running = false
        if (!AccService.running) {
            getAndShowServiceDestroyedNotification(this)
        }

        super.onDestroy()
    }


}