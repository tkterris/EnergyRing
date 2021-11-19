package cn.vove7.energy_ring.service

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import cn.vove7.energy_ring.floatwindow.FloatRingWindow

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
        if (ForegroundService.running) {
            FloatRingWindow.update(forceRefresh = true, reload = true)
        }
    }

    override fun onDestroy() {
        Log.d("Debug", "AccService destroyed")
        running = false
        super.onDestroy()
        FloatRingWindow.update(forceRefresh = true, reload = true)
    }

    override fun onInterrupt() {

    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
    }
}