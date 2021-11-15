package cn.vove7.energy_ring.listener

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.PowerManager
import android.util.Log
import cn.vove7.energy_ring.App
import cn.vove7.energy_ring.floatwindow.FloatRingWindow
import cn.vove7.energy_ring.util.Config

/**
 * # PowerSaveModeListener
 *
 * @author Vove
 * 2020/9/8
 */
object PowerSaveModeListener : EnergyRingBroadcastReceiver() {

    override fun start() {
        val intentFilter = IntentFilter(PowerManager.ACTION_POWER_SAVE_MODE_CHANGED)
        App.INS.registerReceiver(this, intentFilter)
    }

    val powerSaveMode: Boolean get() = App.powerManager.isPowerSaveMode

    override fun onReceive(p0: Context?, p1: Intent?) {
        FloatRingWindow.onDeviceStateChange()
    }
}