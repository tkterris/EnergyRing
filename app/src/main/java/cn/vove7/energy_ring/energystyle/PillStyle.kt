package cn.vove7.energy_ring.energystyle

import android.util.Log
import android.view.View
import android.widget.FrameLayout
import cn.vove7.energy_ring.App
import cn.vove7.energy_ring.ui.view.PillView
import cn.vove7.energy_ring.util.state.Config
import cn.vove7.energy_ring.util.getColorByRange
import cn.vove7.energy_ring.util.weakLazy

/**
 * # PillStyle
 *
 * @author Vove
 * 2020/5/11
 */
class PillStyle : RotateAnimatorSupporter() {
    private val pvDelegate = weakLazy {
        PillView(App.INS)
    }
    private val pillView by pvDelegate

    override val displayView: View
        get() = pillView

    override fun onAnimatorUpdate(rotateValue: Float) {
        pillView.pillRotation = rotateValue
        pillView.postInvalidate()
    }

    override fun setColor(color: Int) {
        (displayView as PillView).apply {
            mainColor = color
        }
    }

    override fun update(progress: Float?) {
        (displayView as PillView).apply {
            strokeWidthF = Config.INS.device.strokeWidth

            progress?.also {
                this.progress = it
            }
            mainColor = getColorByRange(this.progress)
            bgColor = Config.INS.bgColor

            val h = Config.INS.size
            val w = Config.INS.spacingWidth + h
            layoutParams = layoutParams?.also {
                if (it.width != w || it.height != h) {
                    Log.d(TAG, "update  ----> ${w to h}")
                    it.width = w
                    it.height = h
                }
            } ?: FrameLayout.LayoutParams(w, h)
            requestLayout()
            invalidate()
        }
    }

    override fun onRemove() {
        super.onRemove()
        pvDelegate.clearWeakValue()
    }
}