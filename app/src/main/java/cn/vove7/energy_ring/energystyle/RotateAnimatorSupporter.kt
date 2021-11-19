package cn.vove7.energy_ring.energystyle

import android.animation.Animator
import android.animation.ValueAnimator
import android.util.Log
import android.view.animation.LinearInterpolator
import androidx.annotation.CallSuper
import cn.vove7.energy_ring.floatwindow.FloatRingWindow
import cn.vove7.energy_ring.listener.PowerEventReceiver
import cn.vove7.energy_ring.util.Config

/**
 * # RotateAnimatorSupporter
 *
 * @author Vove
 * 2020/5/12
 */
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
        if (!rotateAnimator.isStarted) {
            Log.d(TAG, "Initial start of animation, should only be called once")
            rotateAnimator.start()
        }

        if (!FloatRingWindow.visible) {
            rotateAnimator.pause()
            return
        }

        val rotationDuration = when {
            PowerEventReceiver.isCharging -> Config.chargingRotateDuration
            Config.autoRotateDisCharging -> Config.defaultRotateDuration
            else -> 0L
        }

        Log.d(TAG, "Updating rotation duration  ----> dur: $rotationDuration")
        if (rotationDuration == 0L) {
            rotateAnimator.pause()
            rotateAnimator.setCurrentFraction(0f)
        } else {
            if (rotationDuration != rotateAnimator.duration) {
                val animatedFraction = rotateAnimator.animatedFraction
                rotateAnimator.setDuration(rotationDuration.toLong())
                    .setCurrentFraction(animatedFraction)
            }
            rotateAnimator.resume()
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
        rotateAnimator.pause()
    }

    @CallSuper
    override fun onRemove() {
        Log.d(TAG, "onRemove  ----> $TAG")
        rotateAnimator.pause()
    }
}