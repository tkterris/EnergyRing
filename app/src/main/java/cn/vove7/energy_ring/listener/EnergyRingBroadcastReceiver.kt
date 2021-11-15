package cn.vove7.energy_ring.listener

import android.content.BroadcastReceiver
import android.content.Context
import cn.vove7.energy_ring.App

abstract class EnergyRingBroadcastReceiver : BroadcastReceiver() {

    abstract fun start()

    open fun stop () {
        try {
            App.INS.unregisterReceiver(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}