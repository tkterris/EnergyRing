package cn.vove7.energy_ring.service

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import cn.vove7.energy_ring.App
import cn.vove7.energy_ring.floatwindow.FloatRingWindow
import cn.vove7.energy_ring.util.startEnergyForegroundService
import cn.vove7.energy_ring.util.stopEnergyForegroundService

/**
 * # AccService
 *
 * Created on 2020/7/30
 * @author Vove
 */
class AccService : AccessibilityService() {
    companion object {
        lateinit var INS : AccService
        var running : Boolean = false
    }

    override fun onCreate() {
        Log.d("Debug", "AccService created")
        INS = this
        super.onCreate()
    }

    override fun onServiceConnected() {
        Log.d("Debug", "AccService started")
        running = true
        stopEnergyForegroundService()
        FloatRingWindow.checkPermissionAndUpdate()
    }

    override fun onDestroy() {
        Log.d("Debug", "AccService destroyed")
        running = false
        super.onDestroy()
        startEnergyForegroundService()
    }

    override fun onInterrupt() {

    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
    }
}