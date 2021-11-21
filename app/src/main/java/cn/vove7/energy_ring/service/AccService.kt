package cn.vove7.energy_ring.service

import android.accessibilityservice.AccessibilityService
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import cn.vove7.energy_ring.App
import cn.vove7.energy_ring.listener.*
import cn.vove7.energy_ring.util.sendEnergyBroadcast
import cn.vove7.energy_ring.util.state.ApplicationState

/**
 * # AccService
 *
 * Created on 2020/7/30
 * @author Vove
 */
class AccService : AccessibilityService() {
    companion object {
        lateinit var INS : AccService
            private set
        var running : Boolean = false
            private set
        val enabled : Boolean get() = Settings.Secure.getString(App.INS.contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES).split(":")
                .any { service -> service.startsWith(App.INS.packageName + "/") }
    }

    override fun onCreate() {
        Log.d("Debug", "AccService created")
        INS = this
        super.onCreate()
    }

    override fun onServiceConnected() {
        Log.d("Debug", "AccService started")
        running = true

        ScreenListener.start()
        PowerEventReceiver.start()
        RotationListener.start()
        FloatRingWindow.start()
        HideBatteryReceiver.start()
        ApplicationState.applyConfig()

        super.onServiceConnected()
    }

    override fun onDestroy() {
        Log.d("Debug", "AccService destroyed")
        running = false

        sendEnergyBroadcast(BroadcastActions.DISPLAY_UPDATE)
        ScreenListener.stop()
        PowerEventReceiver.stop()
        RotationListener.stop()
        FloatRingWindow.stop()
        HideBatteryReceiver.stop()

        super.onDestroy()
    }

    override fun onInterrupt() {

    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
    }
}