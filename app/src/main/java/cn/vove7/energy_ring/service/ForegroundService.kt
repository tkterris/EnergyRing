package cn.vove7.energy_ring.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import cn.vove7.energy_ring.floatwindow.FloatRingWindow
import cn.vove7.energy_ring.listener.PowerEventReceiver
import cn.vove7.energy_ring.listener.PowerSaveModeListener
import cn.vove7.energy_ring.listener.RotationListener
import cn.vove7.energy_ring.listener.ScreenListener
import cn.vove7.energy_ring.util.Config
import cn.vove7.energy_ring.util.showServiceDestroyedNotification
import cn.vove7.energy_ring.util.getForeNotification

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
        if (Config.autoHideRotate) {
            RotationListener.start()
        }
        PowerSaveModeListener.start(this)

        startForeground(1000, getForeNotification(this))
        return START_STICKY
    }

    override fun onDestroy() {
        showServiceDestroyedNotification(this)
        super.onDestroy()
    }

}