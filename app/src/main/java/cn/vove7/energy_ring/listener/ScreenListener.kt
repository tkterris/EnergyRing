package cn.vove7.energy_ring.listener

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.PowerManager
import cn.vove7.energy_ring.App
import cn.vove7.energy_ring.util.sendEnergyBroadcast

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
        }
        App.INS.registerReceiver(this, intentFilter)
    }

    val screenOn: Boolean get() = App.powerManager.isInteractive

    override fun onReceive(context: Context?, intent: Intent?) {
        sendEnergyBroadcast(BroadcastActions.DISPLAY_UPDATE)
    }
}