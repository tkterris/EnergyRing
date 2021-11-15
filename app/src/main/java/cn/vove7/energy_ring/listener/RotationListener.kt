package cn.vove7.energy_ring.listener

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.util.Log
import android.view.Surface
import android.view.WindowManager
import cn.vove7.energy_ring.App
import cn.vove7.energy_ring.floatwindow.FloatRingWindow
import cn.vove7.energy_ring.floatwindow.FullScreenListenerFloatWin


/**
 * # RotationListener
 *
 * @author Vove
 * 2020/5/9
 */
object RotationListener : EnergyRingBroadcastReceiver() {

    var enabled = false

    override fun start() {
        val intentFilter = IntentFilter("android.intent.action.CONFIGURATION_CHANGED")
        App.INS.registerReceiver(this, intentFilter)
        enabled = true
    }

    override fun stop() {
        enabled = false
        super.stop()
    }

    var rotation = 0

    val canShow: Boolean get() = !enabled || (rotation == Surface.ROTATION_0)

    override fun onReceive(context: Context?, intent: Intent?) {
        rotation = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            context?.display?.rotation ?: Surface.ROTATION_0
        } else {
            val wm = App.INS.getSystemService(WindowManager::class.java)!!
            if (wm.defaultDisplay.rotation == 0) { Surface.ROTATION_0 } else { Surface.ROTATION_90 }
        }
        FloatRingWindow.onDeviceStateChange()
    }
}