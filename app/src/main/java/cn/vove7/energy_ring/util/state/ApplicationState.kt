package cn.vove7.energy_ring.util.state

import android.content.Context
import android.content.SharedPreferences
import cn.vove7.energy_ring.App
import cn.vove7.energy_ring.floatwindow.FloatRingWindow
import cn.vove7.energy_ring.service.AccService
import com.google.gson.Gson
import com.google.gson.GsonBuilder

/**
 * # Config
 *
 * @author Vove
 * 2020/5/8
 */
object ApplicationState {
    var activeConfig: Config
    var savedConfigs: Array<Config>
    //TODO: UI toggle for this
    var enabled: Boolean = true

    init {
        val sharedPreferences = sharedPreferences()
        //TODO: swap lines once UI toggle is set up
        enabled = sharedPreferences.getBoolean("enabled", true)
        //enabled = sharedPreferences.getBoolean("enabled", false) && AccService.enabled
        activeConfig = Config.jsonDeserialize(sharedPreferences.getString("activeConfig", "{}")!!)
        savedConfigs = sharedPreferences.getStringSet("savedConfigs", emptySet())!!.map {
            Config.jsonDeserialize(it)
        }.toTypedArray()
    }

    private fun persistState() {
        with(sharedPreferences().edit()) {
            this.putBoolean("enabled", enabled)
            this.putString("activeConfig", activeConfig.jsonSerialize())
            this.putStringSet("savedConfigs", savedConfigs.map { it.jsonSerialize() }.toSet())
            apply()
        }
    }

    fun addSavedConfig(config: Config) {
        savedConfigs = savedConfigs.toMutableList().apply { add(config) }.toTypedArray()
    }

    fun applyConfig(newConfig : Config = Config.INS) {
        activeConfig = if (activeConfig != newConfig) newConfig.copy() else activeConfig
        persistState()
        FloatRingWindow.update(layoutChange = true)
    }

    private fun sharedPreferences() : SharedPreferences {
        return App.INS.getSharedPreferences(App.INS.packageName, Context.MODE_PRIVATE)
    }
}
