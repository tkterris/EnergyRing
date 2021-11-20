package cn.vove7.energy_ring.energystyle

import android.view.View
import android.widget.FrameLayout
import cn.vove7.energy_ring.App
import cn.vove7.energy_ring.ui.view.RingView
import cn.vove7.energy_ring.util.Config
import cn.vove7.energy_ring.util.getColorByRange
import cn.vove7.energy_ring.util.weakLazy

/**
 * # RingStyle
 *
 * @author Vove
 * 2020/5/11
 */
class RingStyle : EnergyStyle, RotateAnimatorSupporter() {

    private val ringViewDelegate = weakLazy {
        RingView(App.INS).apply {
            layoutParams = FrameLayout.LayoutParams(Config.INS.size, Config.INS.size)
        }
    }

    override val displayView: View by ringViewDelegate

    override fun onAnimatorUpdate(rotateValue: Float) {
        displayView.rotation = rotateValue
    }

    override fun setColor(color: Int) {
        (displayView as RingView).apply {
            mainColor = color
        }
    }

    override fun update(progress: Float?) {
        (displayView as RingView).apply {
            strokeWidthF = Config.INS.strokeWidth
            if (progress != null) {
                this.progress = progress
            }
            if (Config.INS.colorMode == 2) {
                doughnutColors = Config.INS.colorsDischarging
            } else {
                mainColor = getColorByRange(this.progress)
            }
            bgColor = Config.INS.bgColor
            reSize(Config.INS.size)
            requestLayout()
        }
    }

    override fun onRemove() {
        super.onRemove()
        ringViewDelegate.clearWeakValue()
    }
}