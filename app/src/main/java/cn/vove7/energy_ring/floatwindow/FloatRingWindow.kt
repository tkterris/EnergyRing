package cn.vove7.energy_ring.floatwindow

import android.graphics.PixelFormat
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.WindowManager.LayoutParams
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
import cn.vove7.energy_ring.util.*


/**
 * # FloatRingWindow
 *
 * @author Vove
 * 2020/5/8
 */
object FloatRingWindow {

    private val displayEnergyStyleDelegate = weakLazy {
        buildEnergyStyle()
    }

    private fun buildEnergyStyle(): EnergyStyle = when (Config.energyType) {
        ShapeType.RING -> RingStyle()
        ShapeType.DOUBLE_RING -> DoubleRingStyle()
        ShapeType.PILL -> PillStyle()
    }

    private val displayEnergyStyle by displayEnergyStyleDelegate

    var isShowing = false
    private val layoutParams: LayoutParams
        get() = LayoutParams(
                -2, -2,
                Config.posX, Config.posY,
                LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
                LayoutParams.FLAG_NOT_TOUCH_MODAL or
                        LayoutParams.FLAG_NOT_FOCUSABLE or
                        LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                        LayoutParams.FLAG_LAYOUT_INSET_DECOR or
                        LayoutParams.FLAG_NOT_TOUCHABLE, 0
        ).apply {
            format = PixelFormat.RGBA_8888
            gravity = Gravity.TOP or Gravity.START
        }

    private val bodyView by lazy {
        FrameLayout(App.INS).apply {
            addView(displayEnergyStyle.displayView, -2, -2)
        }
    }

    @Synchronized fun update(layoutChange : Boolean = false) {
        if (layoutChange) {
            refreshLayout()
        }
        if (!canShow()) {
            hide()
        } else {
            show()
        }
    }

    private fun refreshLayout() {
        hide()
        try {
            bodyView.tag = false
            isShowing = false
            wm.removeViewImmediate(bodyView)
        } catch (e: Exception) {
            Log.w("FloatRingWindow","Failed to remove view from WindowManager, " +
                    "enable debug to view error")
            Log.d("FloatRingWindow", "View removal failure", e)
        }
        displayEnergyStyle.onRemove()
        displayEnergyStyleDelegate.clearWeakValue()
        bodyView.apply {
            removeAllViews()
            addView(displayEnergyStyle.displayView, -2, -2)
        }

        if (bodyView.tag == true) {
            wm.updateViewLayout(bodyView, layoutParams)
        } else {
            wm.addView(bodyView, layoutParams)
            bodyView.tag = true
        }
        bodyView.requestLayout()
    }

    private fun show() {
        try {
            bodyView.visibility = View.VISIBLE
            isShowing = true
            displayEnergyStyle.update(batteryLevel)
            displayEnergyStyle.reloadAnimation()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun hide() {
        if (!isShowing) {
            return
        }
        bodyView.visibility = View.INVISIBLE
        isShowing = false
        displayEnergyStyle.onHide()
    }

    private fun canShow(): Boolean {
        val cond1 = !Config.autoHideRotate || !RotationListener.isRotated
        val cond3 = !Config.powerSaveHide || !PowerEventReceiver.powerSaveMode
        val cond4 = !Config.screenOffHide || ScreenListener.screenOn
        val fullyTransparent = isTransparent(Config.ringBgColor)
                && isTransparent(getColorByRange(batteryLevel))
        val serviceRunning = AccService.running

        Log.d("Debug :", "canShow  ----> 旋转: $cond1 省电: $cond3 screen on: $cond4 " +
                "transparent: $fullyTransparent service running: $serviceRunning")

        return cond1 && cond3 && cond4 && !fullyTransparent && serviceRunning

    }

}