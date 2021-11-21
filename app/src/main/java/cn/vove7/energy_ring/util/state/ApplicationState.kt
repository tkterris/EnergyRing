package cn.vove7.energy_ring.util.state

import android.content.Context
import android.content.SharedPreferences
import cn.vove7.energy_ring.App
import cn.vove7.energy_ring.listener.BroadcastActions
import cn.vove7.energy_ring.util.sendEnergyBroadcast

/**
 * # Config
 *
 * @author Vove
 * 2020/5/8
 */
object ApplicationState {
    var activeConfig: Config
    //TODO: UI toggle for this
    var enabled: Boolean = true

    init {
        val sharedPreferences = sharedPreferences()
        //TODO: swap lines once UI toggle is set up
        enabled = sharedPreferences.getBoolean("enabled", true)
        //enabled = sharedPreferences.getBoolean("enabled", false) && AccService.enabled
        //TODO: if null, use preset if one matches
        activeConfig = Config.jsonDeserialize(sharedPreferences.getString("activeConfig", "{}")!!)
    }

    private fun persistState() {
        with(sharedPreferences().edit()) {
            this.putBoolean("enabled", enabled)
            this.putString("activeConfig", activeConfig.jsonSerialize())
            apply()
        }
    }

    fun applyConfig(newConfig : Config = Config.INS) {
        activeConfig = if (activeConfig != newConfig) newConfig.copy() else activeConfig
        persistState()
        sendEnergyBroadcast(BroadcastActions.DISPLAY_REFRESH)
    }

    private fun sharedPreferences() : SharedPreferences {
        return App.INS.getSharedPreferences(App.INS.packageName, Context.MODE_PRIVATE)
    }
}
