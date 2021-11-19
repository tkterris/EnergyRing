package cn.vove7.energy_ring.listener

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.view.Surface
import cn.vove7.energy_ring.App
import cn.vove7.energy_ring.floatwindow.FloatRingWindow
import cn.vove7.energy_ring.util.display


/**
 * # RotationListener
 *
 * @author Vove
 * 2020/5/9
 */
object RotationListener : EnergyRingBroadcastReceiver() {

    override fun start() {
        val intentFilter = IntentFilter(Intent.ACTION_CONFIGURATION_CHANGED)
        App.INS.registerReceiver(this, intentFilter)
    }

    private var rotation = Surface.ROTATION_0
    val isRotated: Boolean get() = rotation != Surface.ROTATION_0

    override fun onReceive(context: Context?, intent: Intent?) {
        val newRotation = display.rotation

        if (rotation != newRotation) {
            rotation = newRotation
            FloatRingWindow.update()
        }
    }
}