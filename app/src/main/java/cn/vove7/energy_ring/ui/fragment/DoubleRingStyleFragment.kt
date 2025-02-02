package cn.vove7.energy_ring.ui.fragment

import android.view.View
import cn.vove7.energy_ring.R
import cn.vove7.energy_ring.util.state.ApplicationState
import cn.vove7.energy_ring.util.state.Config
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItems
import kotlinx.android.synthetic.main.fragment_double_ring_style.*
import kotlinx.android.synthetic.main.fragment_double_ring_style.view.*

/**
 * # DoubleRingStyleFragment
 *
 * @author Vove
 * 2020/5/12
 */
class DoubleRingStyleFragment : BaseStyleFragment() {
    override val layoutRes: Int
        get() = R.layout.fragment_double_ring_style


    override fun refreshData() {
        super.refreshData()

        val fc by lazy { resources.getStringArray(R.array.ring_features)[Config.INS.secondaryRingFeature] }
        pick_secondary_ring_func_view?.text = getString(R.string.feature_of_secondary_ring, fc)

        val dir by lazy { resources.getStringArray(R.array.double_ring_battery_direction)[Config.INS.doubleRingChargingIndex] }
        pick_battery_direction_view?.text = getString(R.string.battery_direction_format, dir)
    }

    override fun listenSeekBar(view: View) {
        super.listenSeekBar(view)
        view.apply {
            pick_secondary_ring_func_view.setOnClickListener {
                MaterialDialog(context).show {
                    listItems(R.array.ring_features, waitForPositiveButton = false) { _, i, _ ->
                        if (Config.INS.secondaryRingFeature != i) {
                            Config.INS.secondaryRingFeature = i
                            val fc = resources.getStringArray(R.array.ring_features)[i]
                            view.pick_secondary_ring_func_view.text = getString(R.string.feature_of_secondary_ring, fc)
                            ApplicationState.applyConfig()
                        }
                    }
                }
            }
            pick_battery_direction_view.setOnClickListener {
                MaterialDialog(context).show {
                    listItems(R.array.double_ring_battery_direction, waitForPositiveButton = false) { _, i, _ ->
                        if (Config.INS.doubleRingChargingIndex != i) {
                            Config.INS.doubleRingChargingIndex = i
                            val dir = resources.getStringArray(R.array.double_ring_battery_direction)[i]
                            view.pick_battery_direction_view?.text = getString(R.string.battery_direction_format, dir)
                            ApplicationState.applyConfig()
                        }
                    }
                }
            }
        }
    }
}