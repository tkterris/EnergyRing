package cn.vove7.energy_ring.util.state

import android.os.Build
import android.os.PowerManager
import cn.vove7.energy_ring.listener.PowerEventReceiver
import cn.vove7.energy_ring.listener.ScreenListener
import cn.vove7.energy_ring.model.ShapeType
import cn.vove7.energy_ring.util.asColor
import cn.vove7.energy_ring.util.screenHeight
import cn.vove7.energy_ring.util.screenWidth
import com.google.gson.Gson
import com.google.gson.GsonBuilder

/**
 * # Config
 *
 * @author Vove
 * 2020/5/8
 */
class Config(
    var device : DeviceData = DeviceData(),
    var bgColor: Int = "#a0fffde7".asColor,
    var doubleRingChargingIndex: Int = 0,
    var secondaryRingFeature: Int = 0,
    var colorMode: Int = 1,
    var showRotated: Boolean = false,
    var showScreenOff: Boolean = false,
    var showBatterySaver: Boolean = false,
    var hideBatteryAod: Boolean = false,
    var chargingRotateSpeed: Int = 180,
    var dischargingRotateSpeed: Int = 8,
    var colorsDischarging: IntArray = intArrayOf(
        "#ff00e676".asColor,
        "#ff64dd17".asColor
    ),
    var colorsCharging: IntArray = intArrayOf(
        "#ff00e676".asColor,
        "#ff64dd17".asColor
    )
) {
    class DeviceData (
        var buildModel: String = Build.MODEL,
        var buildId: String = Build.ID,
        var posXf: Int = 148,
        var posYf: Int = 22,
        var strokeWidth: Float = 12f,
        var sizef: Float = 0.06736f,
        var energyType: ShapeType = ShapeType.RING,
        var spacingWidthF: Int = 10,
            )

    //helper methods
    val posX get() = ((device.posXf / 2000f) * screenWidth).toInt()
    val posY get() = ((device.posYf / 2000f) * screenHeight).toInt()
    val spacingWidth get() = ((device.spacingWidthF / 2000f) * screenWidth).toInt()
    var size: Int
        get() = ((device.sizef * screenWidth).toInt())
        set(value) {
            device.sizef = value.toFloat() / screenWidth
        }
    val rotationSpeed get() : Int = when {
        PowerEventReceiver.isCharging -> chargingRotateSpeed
        !PowerEventReceiver.isCharging && ScreenListener.screenOn -> dischargingRotateSpeed
        else -> 0
    }

    fun copy() : Config {
        val copy : Config = jsonDeserialize(jsonSerialize())
        copy.device.buildModel = Build.MODEL
        copy.device.buildId = Build.ID
        return copy
    }

    fun jsonSerialize() : String {
        return GsonBuilder().setPrettyPrinting().create().toJson(this)
    }

    companion object {
        val INS : Config get() = ApplicationState.activeConfig

        fun jsonDeserialize(json: String) : Config {
            return Gson().fromJson(json, Config::class.java)
        }
    }
}
