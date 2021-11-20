package cn.vove7.energy_ring.energystyle

import android.animation.ValueAnimator
import android.text.format.DateUtils
import android.util.Log
import android.view.animation.LinearInterpolator
import androidx.annotation.CallSuper
import cn.vove7.energy_ring.floatwindow.FloatRingWindow
import cn.vove7.energy_ring.listener.PowerEventReceiver
import cn.vove7.energy_ring.util.state.Config

/**
 * # RotateAnimatorSupporter
 *
 * @author Vove
 * 2020/5/12
 */
abstract class RotateAnimatorSupporter : EnergyStyle {

    val TAG : String = this::class.java.simpleName
    val DEGREES = 360L
    val ROTATE_SPEED_SCALE = 1L

    companion object {
    }

    private val rotateAnimator: ValueAnimator = ValueAnimator.ofFloat(0f, 360f).apply {
        repeatCount = ValueAnimator.INFINITE
        interpolator = LinearInterpolator()
        addUpdateListener {
            onAnimatorUpdate(it.animatedValue as Float)
        }
    }

    abstract fun onAnimatorUpdate(rotateValue: Float)

    @CallSuper
    override fun reloadAnimation() {
        //TODO: possible performance improvements here?
        if (!FloatRingWindow.visible) {
            rotateAnimator.cancel()
            return
        }

        val rotationSpeed = Config.INS.rotationSpeed

        Log.d(TAG, "Updating rotation speed  ----> speed: $rotationSpeed")
        if (rotationSpeed == 0L) {
            rotateAnimator.cancel()
            rotateAnimator.setCurrentFraction(0f)
        } else {
            val rotationDuration
                = DateUtils.SECOND_IN_MILLIS * DEGREES / (rotationSpeed * ROTATE_SPEED_SCALE)
            if (rotationDuration != rotateAnimator.duration) {
                val animatedFraction = rotateAnimator.animatedFraction
                rotateAnimator.setDuration(rotationDuration.toLong())
                    .setCurrentFraction(animatedFraction)
            }
            if (!rotateAnimator.isStarted) {
                rotateAnimator.start()
            }
            if (rotateAnimator.isPaused) {
                rotateAnimator.resume()
            }
        }
    }

    @CallSuper
    override fun resumeAnimator() {
        rotateAnimator.resume()
    }

    override fun pauseAnimator() {
        Log.d(TAG, "pauseAnimator  ----> $TAG")
        rotateAnimator.pause()
    }

    @CallSuper
    override fun onHide() {
        Log.d(TAG, "onHide  ----> $TAG")
        rotateAnimator.cancel()
    }

    @CallSuper
    override fun onRemove() {
        Log.d(TAG, "onRemove  ----> $TAG")
        rotateAnimator.cancel()
    }
}