package cn.vove7.energy_ring.service

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.util.Log
import android.view.WindowManager
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
        var INS: AccService? = null
        val hasOpend get() = INS != null
        var wm: WindowManager? = null
    }

    override fun onServiceConnected() {
        Log.d("Debug", "AccService started")
        INS = this
        wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        if (ForegroundService.running) {
            FloatRingWindow.reload()
        }
    }

    override fun onDestroy() {
        INS = null
        wm = null
        super.onDestroy()
        FloatRingWindow.reload()
    }

    override fun onCreate() {
        Log.d("Debug", "AccService created")
        super.onCreate()
    }

    override fun onInterrupt() {

    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
    }
}