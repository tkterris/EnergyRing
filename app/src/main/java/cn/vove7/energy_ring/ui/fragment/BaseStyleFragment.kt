package cn.vove7.energy_ring.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import cn.vove7.energy_ring.ui.adapter.ColorsAdapter
import cn.vove7.energy_ring.util.state.Config
import cn.vove7.energy_ring.util.antiColor
import cn.vove7.energy_ring.util.pickColor
import cn.vove7.energy_ring.util.state.ApplicationState
import kotlinx.android.synthetic.main.fragment_double_ring_style.*
import kotlinx.android.synthetic.main.fragment_double_ring_style.view.*

/**
 * # BaseStyleFragment
 *
 * @author Vove
 * 2020/5/14
 */
abstract class BaseStyleFragment : Fragment() {
    abstract val layoutRes: Int

    final override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(layoutRes, container, false)
    }

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        color_list.adapter = ColorsAdapter({ Config.INS.colorsDischarging }, { Config.INS.colorsDischarging = it })
        color_list_charging.adapter = ColorsAdapter({ Config.INS.colorsCharging }, { Config.INS.colorsCharging = it })
        refreshData()
        listenSeekBar(view)
    }

    override fun onResume() {
        super.onResume()
        refreshData()
    }

    @CallSuper
    open fun refreshData() = view.run {
        bg_color_view?.setBackgroundColor(Config.INS.bgColor)
        bg_color_view?.setTextColor(Config.INS.bgColor.antiColor)
        color_list?.adapter?.notifyDataSetChanged()
        color_list_charging?.adapter?.notifyDataSetChanged()
        strokeWidth_seek_bar?.progress = Config.INS.device.strokeWidth.toInt()

        posx_seek_bar?.progress = Config.INS.device.posXf
        posy_seek_bar?.progress = Config.INS.device.posYf
        size_seek_bar?.progress = Config.INS.size

        charging_rotateDuration_seek_bar?.progress = Config.INS.chargingRotateSpeed
        discharging_rotateDuration_seek_bar?.progress = Config.INS.dischargingRotateSpeed

        spacing_seek_bar?.progress = Config.INS.device.spacingWidthF
    }

    @CallSuper
    open fun listenSeekBar(view: View): Unit = view.run {

        bg_color_view.setOnClickListener {
            pickColor(context!!, initColor = Config.INS.bgColor) { c ->
                bg_color_view.setBackgroundColor(c)
                bg_color_view.setTextColor(c.antiColor)
                Config.INS.bgColor = c
                ApplicationState.applyConfig()
            }
        }
        charging_rotateDuration_seek_bar?.onStop { progress ->
            Config.INS.chargingRotateSpeed = progress
            ApplicationState.applyConfig()
        }
        discharging_rotateDuration_seek_bar?.onStop { progress ->
            Config.INS.dischargingRotateSpeed = progress
            Log.d("Debug :", "listenSeekBar  ---->$progress ${Config.INS.dischargingRotateSpeed}")
            ApplicationState.applyConfig()
        }
        strokeWidth_seek_bar?.onChange { progress, user ->
            if (!user) return@onChange
            Config.INS.device.strokeWidth = progress.toFloat()
            ApplicationState.applyConfig()
        }
        strokeWidth_seek_bar?.onStart {
            ApplicationState.applyConfig()
        }
        posx_seek_bar?.onChange { progress, user ->
            if (!user) return@onChange
            Config.INS.device.posXf = progress
            ApplicationState.applyConfig()
        }
        posy_seek_bar?.onChange { progress, user ->
            if (!user) return@onChange
            Config.INS.device.posYf = progress
            ApplicationState.applyConfig()
        }
        size_seek_bar?.onStart {
            ApplicationState.applyConfig()
        }
        size_seek_bar?.onChange { progress, user ->
            if (!user) return@onChange
            Config.INS.size = progress
            ApplicationState.applyConfig()
        }
        spacing_seek_bar?.onStart {
            ApplicationState.applyConfig()
        }
        spacing_seek_bar?.onChange { progress, user ->
            if (!user) return@onChange
            Config.INS.device.spacingWidthF = progress
            ApplicationState.applyConfig()
        }
    } ?: Unit

}