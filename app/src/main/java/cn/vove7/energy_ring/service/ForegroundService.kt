package cn.vove7.energy_ring.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import cn.vove7.energy_ring.floatwindow.FloatRingWindow
import cn.vove7.energy_ring.listener.PowerEventReceiver
import cn.vove7.energy_ring.listener.RotationListener
import cn.vove7.energy_ring.listener.ScreenListener
import cn.vove7.energy_ring.util.Config
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

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        FloatRingWindow.start()
        ScreenListener.start()
        PowerEventReceiver.start()
        RotationListener.start()

        startForeground(FOREGROUND_NOTIFICATION_ID, getAndShowForeNotification(this))

        return START_STICKY
    }

    override fun onDestroy() {

        FloatRingWindow.hide()
        ScreenListener.stop()
        PowerEventReceiver.stop()
        RotationListener.stop()

        getAndShowServiceDestroyedNotification(this)
        super.onDestroy()
    }

}