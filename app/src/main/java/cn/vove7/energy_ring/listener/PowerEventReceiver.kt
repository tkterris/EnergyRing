package cn.vove7.energy_ring.listener

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.PowerManager
import android.util.Log
import cn.vove7.energy_ring.App
import cn.vove7.energy_ring.util.initialBatteryLevel
import cn.vove7.energy_ring.util.initialIsCharging
import cn.vove7.energy_ring.util.sendEnergyBroadcast

/**
 * # PowerEventReceiver
 * 充电状态监听
 * fixed 启动App时无法获得当前充电状态
 * @author Vove7
 */
object PowerEventReceiver : EnergyRingBroadcastReceiver() {

    /**
     * 注册广播接收器
     */
    override fun start() {
        App.INS.registerReceiver(this, intentFilter)
    }

    private val intentFilter: IntentFilter
        get() = IntentFilter().apply {
            addAction(Intent.ACTION_POWER_CONNECTED)
            addAction(Intent.ACTION_POWER_DISCONNECTED)
            addAction(Intent.ACTION_BATTERY_CHANGED)
            addAction(PowerManager.ACTION_POWER_SAVE_MODE_CHANGED)
        }

    var isCharging: Boolean = initialIsCharging
    var batteryLevel: Float = initialBatteryLevel
    val powerSaveMode: Boolean get() = App.powerManager.isPowerSaveMode

    override fun onReceive(context: Context?, intent: Intent?) {
        //打开充电自动开启唤醒
        when (intent?.action) {
            Intent.ACTION_POWER_CONNECTED -> {//连接充电器
                Log.d("Debug :", "onReceive  ----> onCharging")
                isCharging = true
                sendEnergyBroadcast(BroadcastActions.DISPLAY_UPDATE)
            }
            Intent.ACTION_POWER_DISCONNECTED -> {//断开
                Log.d("Debug :", "onReceive  ----> onDisCharging")
                isCharging = false
                sendEnergyBroadcast(BroadcastActions.DISPLAY_UPDATE)
            }
            Intent.ACTION_BATTERY_CHANGED -> {
                val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) //电量的刻度
                val maxLevel = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1) //最大
                val l = level.toFloat() / maxLevel.toFloat()
                if (l != batteryLevel) {
                    batteryLevel = l
                    Log.d("Debug :", "onReceive  ----> ACTION_BATTERY_CHANGED $l")
                    sendEnergyBroadcast(BroadcastActions.DISPLAY_UPDATE)
                }
            }
            PowerManager.ACTION_POWER_SAVE_MODE_CHANGED -> {
                Log.d("Debug :", "onReceive  ----> ACTION_POWER_SAVE_MODE_CHANGED")
                sendEnergyBroadcast(BroadcastActions.DISPLAY_UPDATE)
            }
        }

    }

}
