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
    var buildModel: String = Build.MODEL,
    var buildId: String = Build.ID,
    var posXf: Int = 148,
    var posYf: Int = 22,
    var strokeWidth: Float = 12f,
    var sizef: Float = 0.06736f,
    var energyType: ShapeType = ShapeType.RING,
    var spacingWidthF: Int = 10,
    var bgColor: Int = "#a0fffde7".asColor,
    var doubleRingChargingIndex: Int = 0,
    var secondaryRingFeature: Int = 0,
    var colorMode: Int = 1,
    var showRotated: Boolean = false,
    var showScreenOff: Boolean = false,
    var showBatterySaver: Boolean = false,
    var chargingRotateSpeed: Long = 8,
    var dischargingRotateSpeed: Long = 180,
    var colorsDischarging: IntArray = intArrayOf(
        "#ff00e676".asColor,
        "#ff64dd17".asColor
    ),
    var colorsCharging: IntArray = intArrayOf(
        "#ff00e676".asColor,
        "#ff64dd17".asColor
    )
) {

    //helper methods
    val posX get() = ((posXf / 2000f) * screenWidth).toInt()
    val posY get() = ((posYf / 2000f) * screenHeight).toInt()
    val spacingWidth get() = ((spacingWidthF / 2000f) * screenWidth).toInt()
    var size: Int
        get() = ((sizef * screenWidth).toInt())
        set(value) {
            sizef = value.toFloat() / screenWidth
        }
    val rotationSpeed get() : Long = when {
        PowerEventReceiver.isCharging -> chargingRotateSpeed
        !PowerEventReceiver.isCharging && ScreenListener.screenOn -> dischargingRotateSpeed
        else -> 0L
    }

    fun copy() : Config {
        val copy : Config = jsonDeserialize(jsonSerialize(this))
        copy.buildModel = Build.MODEL
        copy.buildId = Build.ID
        return copy
    }

    companion object {
        val INS : Config get() = ApplicationState.activeConfig

        fun jsonSerialize(config: Config) : String {
            return GsonBuilder().setPrettyPrinting().create().toJson(config)
        }

        fun jsonDeserialize(json: String) : Config {
            return Gson().fromJson(json, Config::class.java)
        }

        val presetDevices =
            arrayOf(
                    Config("一加8 Pro", "IN2020", 148, 22, 8f * 2, 0.06736f),
                    Config("一加8", "IN2010", 116, 27, 6f * 2, 0.07037037f),
                    Config("小米 10 Pro", "Mi 10 Pro", 176, 20, 7f * 2, 0.07037037f),
                    Config("小米 10", "Mi 10", 148, 22, 8f * 2, 0.06736f),
                    Config("vivo Z6", "V1963A", 1764, 21, 8f * 2, 0.06944445f),
                    Config("vivo Y85", "vivo Y85", 1311, 10, 9f * 2, 0.05462963f),
                    Config("vivo X30", "V1938CT", 1752, 23, 9f * 2, 0.06481481f),
                    Config("荣耀20 Pro", "YAL-AL10", 61, 16, 11f * 2, 0.08888889f),
                    Config("荣耀20", "YAL-AL00", 65, 18, 10f * 2, 0.085185182f),
                    Config("荣耀20S", "YAL-AL50", 57, 14, 20f * 2, 0.094444446f),
                    Config("荣耀V20", "PCT-AL10", 91, 19, 5f * 2, 0.075f),
                    Config("荣耀Play3", "ASK-AL00x", 83, 14, 5f * 2, 0.08611111f),
                    Config("华为Nova7 SE", "CDY-AN00", 60, 16, 12f * 2, 0.087037034f),
                    Config("华为Nova3", "PAR-AL00", 1644, 0, 3f * 2, 0.027777778f),
                    Config("华为Mate30", "TAS-AL00", 148, 22, 8f * 2, 0.06736f),
                    Config("红米 K30", "Readmi K30", 1545, 22, 8f * 2, 0.06726f),
                    Config("红米 Note 8 Pro", "Readmi Note 8 Pro", 1752, 7, 8f * 2, 0.075f),
                    Config("Samsung S20+", "SM-G9860", 938, 10, 7f * 2, 0.062962964f),
                    Config("Samsung S20", "SM-G9810", 936, 12, 8f * 2, 0.06736f),
                    Config("Samsung Galaxy Note 10+ 5G", "SM-N9760", 931, 12, 6f * 2, 0.0712963f),
                    Config("Samsung Galaxy Note 10+", "SM-N975U1", 935, 14, 9f * 2, 0.06736f),
                    Config("Samsung Galaxy Note 10", "SM-N9700", 924, 11, 6f * 2, 0.07777778f),
                    Config("Samsung S20 Ultra 5G", "SM-G9880", 91, 23, 8f * 2, 0.08888889f),
                    Config("Samsung S10", "SM-G9730", 1703, 21, 10f * 2, 0.08958333f),
                    Config("Samsung S10e", "SM-G9708", 1700, 12, 13f * 2, 0.10833334f),
                    Config("Samsung A60", "SM-A6060", 88, 22, 10f * 2, 0.08888889f),
                    Config("VIVO Z5X", "V1911A", 80, 14, 8f * 2, 0.083333336f),
                    Config("IQOO Neo", "V1936A", 1770, 0, 8f * 2, 0.06111111f),
                    Config("IQOO Neo3", "V1981A", 1739, 13, 10f * 2, 0.085185185f),
                    Config("一加7T(配合圆形电池)", "HD1900", 1796, 16, 16f * 2, 0.05277778f),
                    Config("华为MatePad Pro", "MRX-AL09", 1894, 0, 12f * 2, 0.05625f),
                    Config("OPPO Ace2", "PDHM00", 117, 28, 5f * 2, 0.06851852f),
                    Config("OPPO Find X2 Pro", "PDEM30", 148, 22, 8f * 2, 0.06736f),

                    Config("OPPO Reno", "PCAT00", 1887, 46, 6f * 2, 0.028703704f),
                    Config("荣耀30S", "CDY-AN90", 62, 19, 23f, 0.08472222f, ShapeType.RING),
                    Config(
                            "红米K30",
                            "Readmi K30",
                            1567,
                            20,
                            14f,
                            0.07037037f,
                            ShapeType.PILL,
                            spacingWidthF = 58
                    ),
                    Config(
                            "红米K30 5G",
                            "Readmi K30 5G",
                            1577,
                            22,
                            9f,
                            0.06481481f,
                            ShapeType.DOUBLE_RING,
                            spacingWidthF = 220
                    ),
                    Config("Vivo IQOO Z1", "V1986A", 1740, 21, 21f, 0.085185185f, ShapeType.RING),
                    Config(
                            "Huawei P40 Pro",
                            "ELS-AN00",
                            120,
                            17,
                            21f,
                            0.1125f,
                            ShapeType.PILL,
                            spacingWidthF = 336
                    ),
                    Config(
                            "Huawei V30 Pro",
                            "OXF-AN10",
                            72,
                            27,
                            13f,
                            0.08796296f,
                            ShapeType.PILL,
                            spacingWidthF = 252
                    )
            )
    }
}
