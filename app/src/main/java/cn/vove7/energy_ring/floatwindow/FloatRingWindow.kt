package cn.vove7.energy_ring.floatwindow

import android.graphics.PixelFormat
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import cn.vove7.energy_ring.App
import cn.vove7.energy_ring.energystyle.DoubleRingStyle
import cn.vove7.energy_ring.energystyle.EnergyStyle
import cn.vove7.energy_ring.energystyle.PillStyle
import cn.vove7.energy_ring.energystyle.RingStyle
import cn.vove7.energy_ring.listener.PowerEventReceiver
import cn.vove7.energy_ring.listener.RotationListener
import cn.vove7.energy_ring.listener.ScreenListener
import cn.vove7.energy_ring.model.ShapeType
import cn.vove7.energy_ring.service.AccService
import cn.vove7.energy_ring.util.Config
import cn.vove7.energy_ring.util.batteryLevel
import cn.vove7.energy_ring.util.getColorByRange
import cn.vove7.energy_ring.util.isTransparent
import cn.vove7.energy_ring.util.openFloatPermission
import cn.vove7.energy_ring.util.weakLazy
import java.lang.Thread.sleep
import kotlin.concurrent.thread


/**
 * # FloatRingWindow
 *
 * @author Vove
 * 2020/5/8
 */
object FloatRingWindow {

    private val hasPermission
        get() = Settings.canDrawOverlays(App.INS)

    private val displayEnergyStyleDelegate = weakLazy {
        buildEnergyStyle()
    }

    fun buildEnergyStyle(): EnergyStyle = when (Config.energyType) {
        ShapeType.RING -> RingStyle()
        ShapeType.DOUBLE_RING -> DoubleRingStyle()
        ShapeType.PILL -> PillStyle()
    }

    private val displayEnergyStyle by displayEnergyStyleDelegate

    private val wm: WindowManager
        get() = AccService.wm ?: App.windowsManager

    fun start() {
        if (hasPermission) {
            show()
        } else {
            openFloatPermission()
            thread {
                while (!hasPermission) {
                    Log.d("Debug :", "wait p...")
                    sleep(100)
                }
                Log.d("Debug :", "hasPermission")
                if (hasPermission) {
                    Handler(Looper.getMainLooper()).post {
                        show()
                    }
                }
            }
        }
    }

    var isShowing = false
    private val layoutParams: WindowManager.LayoutParams
        get() = WindowManager.LayoutParams(
                -2, -2,
                Config.posX, Config.posY,
                when {
                    AccService.hasOpend -> WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                    else -> WindowManager.LayoutParams.TYPE_PHONE
                },
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                        WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR or
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, 0
        ).apply {
            format = PixelFormat.RGBA_8888
            gravity = Gravity.TOP or Gravity.START
        }

    private val bodyView by lazy {
        FrameLayout(App.INS).apply {
            lastChange = SystemClock.elapsedRealtime()
            addView(displayEnergyStyle.displayView, -2, -2)
        }
    }

    fun onDeviceStateChange() {
        val canShow = canShow()
        if (canShow) {
            forceRefresh()
            show()
        } else if (isShowing) {
            hide()
        }
    }

    fun forceRefresh() {
        lastChange = SystemClock.elapsedRealtime()
        displayEnergyStyle.onRemove()
        displayEnergyStyleDelegate.clearWeakValue()
        bodyView.apply {
            removeAllViews()
            addView(displayEnergyStyle.displayView, -2, -2)
        }
        displayEnergyStyle.update(batteryLevel)
        displayEnergyStyle.reloadAnimation()
    }

    private fun show() {
        isShowing = true
        try {
            bodyView.visibility = View.VISIBLE
            displayEnergyStyle.update(batteryLevel)
            if (bodyView.tag != true) {
                wm.addView(bodyView, layoutParams)
                bodyView.tag = true
            }
            reloadAnimation()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun reload() {
        if (isShowing) {
            try {
                bodyView.tag = false
                isShowing = false
                wm.removeViewImmediate(bodyView)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            show()
        }
    }

    fun update(p: Float? = null) {
        if (!isShowing) {
            return
        }
        wm.updateViewLayout(bodyView, layoutParams)
        checkValid() ?: return
        displayEnergyStyle.update(p)
        bodyView.requestLayout()
    }

    private const val periodRefreshView = 5 * 60 * 1000

    private var lastChange = 0L

    fun checkValid(): Unit? {
        if (SystemClock.elapsedRealtime() - lastChange > periodRefreshView) {
            onDeviceStateChange()
            return null
        }
        return Unit
    }

    fun onCharging() {
        reloadAnimation()
    }


    fun reloadAnimation() {
        displayEnergyStyle.reloadAnimation()
    }

    fun onDisCharging() {
        reloadAnimation()
    }

    fun hide() {
        if (!isShowing) {
            return
        }
        bodyView.visibility = View.INVISIBLE
        isShowing = false
        displayEnergyStyle.onHide()
    }

    private fun canShow(): Boolean {
        val cond0 = hasPermission
        val cond1 = !Config.autoHideRotate || !RotationListener.isRotated
        val cond3 = !Config.powerSaveHide || !PowerEventReceiver.powerSaveMode
        val cond4 = !Config.screenOffHide || ScreenListener.screenOn
        val fullyTransparent = isTransparent(Config.ringBgColor)
                && isTransparent(getColorByRange(batteryLevel))

        Log.d("Debug :", "canShow  ----> hasPermission: $cond0 旋转: $cond1 " +
                "省电: $cond3 screen on: $cond4 transparent: $fullyTransparent")

        return cond0 && cond1 && cond3 && cond4 && !fullyTransparent

    }

}