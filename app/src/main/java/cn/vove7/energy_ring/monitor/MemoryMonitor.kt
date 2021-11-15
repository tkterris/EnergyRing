package cn.vove7.energy_ring.monitor

import android.app.ActivityManager
import android.util.Log
import cn.vove7.energy_ring.App

/**
 * # MemoryMonitor
 *
 * @author Vove
 * 2020/5/12
 */
class MemoryMonitor(override val monitorListener: MonitorListener) : TimerMonitor() {

    val am by lazy {
        App.INS.getSystemService(ActivityManager::class.java)
    }

    override fun getProgress(): Float {
        val mi = ActivityManager.MemoryInfo()
        am.getMemoryInfo(mi)
        val p = ((mi.totalMem - mi.availMem) / mi.totalMem).toFloat()
        Log.d("Debug :", "getProgress  ----> ${mi.availMem}/${mi.totalMem}  $p")
        return p
    }

    override val period: Long get() = 5000L
}