package cn.vove7.energy_ring.listener

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.PowerManager
import android.util.Log
import cn.vove7.energy_ring.App
import cn.vove7.energy_ring.floatwindow.FloatRingWindow

/**
 * # ScreenListener
 *
 * @author Vove
 * 2020/5/14
 */
object ScreenListener : EnergyRingBroadcastReceiver() {

    override fun start()  {
        val intentFilter: IntentFilter = IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_OFF)
            addAction(Intent.ACTION_SCREEN_ON)
            addAction(Intent.ACTION_USER_PRESENT)
            addAction(PowerManager.ACTION_POWER_SAVE_MODE_CHANGED)
        }
        App.INS.registerReceiver(this, intentFilter)
    }

    val screenOn: Boolean get() = App.powerManager.isInteractive
    val screenLocked: Boolean = App.keyguardManager.isDeviceLocked

    override fun onReceive(context: Context?, intent: Intent?) {
        FloatRingWindow.onDeviceStateChange()
    }
}