package cn.vove7.energy_ring.util.state

import com.google.gson.Gson
import com.google.gson.GsonBuilder

/**
 * # Config
 *
 * @author Vove
 * 2020/5/8
 */
object ApplicationState {

    lateinit var activeConfig: Config
    lateinit var savedConfigs: Array<Config>
    //TODO: UI toggle for this
    var enabled: Boolean = true

    init {
        loadState()
    }

    fun jsonSerialize() : String {
        return GsonBuilder().setPrettyPrinting().create().toJson(this)
    }

    fun jsonDeserialize(json: String) : ApplicationState {
        Gson().fromJson(json, ApplicationState::class.java)
        return this
    }

    fun saveConfig(config: Config) {
        savedConfigs = savedConfigs.toMutableList().apply { add(config) }.toTypedArray()
    }

    fun applyConfig(config : Config) {
        activeConfig = config.copy()
        persistState()
    }

    fun persistState() {
        //TODO: fill in
    }

    private fun loadState() {
        //TODO: fill in
        activeConfig = Config()
        savedConfigs = emptyArray()
    }
}
