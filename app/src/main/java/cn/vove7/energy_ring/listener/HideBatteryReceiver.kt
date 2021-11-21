package cn.vove7.energy_ring.listener

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import cn.vove7.energy_ring.App
import cn.vove7.energy_ring.util.state.Config
import cn.vove7.energy_ring.util.wm

object HideBatteryReceiver : EnergyRingBroadcastReceiver() {

    override fun start() {
        val showIntentFilter: IntentFilter = IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_OFF)
            priority = -1
        }
        App.INS.registerReceiver(this, showIntentFilter)
        val hideIntentFilter: IntentFilter = IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_ON)
            addAction(Intent.ACTION_USER_PRESENT)
            priority = 1
        }
        App.INS.registerReceiver(this, hideIntentFilter)
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            Intent.ACTION_SCREEN_OFF -> if (Config.INS.hideBatteryAod) showOverlay()
            Intent.ACTION_SCREEN_ON, Intent.ACTION_USER_PRESENT -> hideOverlay()
        }
    }

    private val view by lazy {
        object : View(App.INS) {
            init {
                setBackgroundColor(Color.BLACK)
            }
        }
    }

    private fun showOverlay() {
        if (!view.isAttachedToWindow) {
            wm.addView(view, layoutParams)
        }
        view.visibility = View.VISIBLE
    }

    private fun hideOverlay() {
        view.visibility = View.INVISIBLE
    }

    private val layoutParams: WindowManager.LayoutParams
        get() = WindowManager.LayoutParams(
            wm.currentWindowMetrics.bounds.width(), 250,
            0, 0,
            WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            , 0
        ).apply {
            format = PixelFormat.RGBA_8888
            gravity = Gravity.BOTTOM or Gravity.CENTER
        }
}