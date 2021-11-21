package cn.vove7.energy_ring.energystyle

import android.animation.ValueAnimator
import android.util.Log
import android.view.animation.LinearInterpolator
import androidx.annotation.CallSuper
import cn.vove7.energy_ring.listener.FloatRingWindow
import cn.vove7.energy_ring.util.state.Config

/**
 * # RotateAnimatorSupporter
 *
 * @author Vove
 * 2020/5/12
 */
const val DEGREES = 360L
const val SECOND_IN_MILLIS = 1000L

abstract class RotateAnimatorSupporter : EnergyStyle {

    val TAG : String = this::class.java.simpleName

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
        if (!FloatRingWindow.visible) {
            rotateAnimator.cancel()
            return
        }

        val rotationSpeed = Config.INS.rotationSpeed

        if (rotationSpeed == 0) {
            Log.d(TAG, "Stopping animation, setting repeatCount to zero")
            rotateAnimator.repeatCount = 0
        } else {
            val rotationDuration = SECOND_IN_MILLIS * DEGREES / rotationSpeed
            Log.d(TAG, "Updating rotation duration  ----> duration: $rotationDuration")
            if (rotationDuration != rotateAnimator.duration) {
                val animatedFraction = rotateAnimator.animatedFraction
                rotateAnimator.setDuration(rotationDuration).setCurrentFraction(animatedFraction)
            }
            rotateAnimator.repeatCount = ValueAnimator.INFINITE
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
        rotateAnimator.setCurrentFraction(0f)
    }

    @CallSuper
    override fun onRemove() {
        Log.d(TAG, "onRemove  ----> $TAG")
        rotateAnimator.cancel()
        rotateAnimator.setCurrentFraction(0f)
    }
}