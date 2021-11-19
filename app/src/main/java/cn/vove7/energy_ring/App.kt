package cn.vove7.energy_ring

import android.app.Application
import android.app.KeyguardManager
import android.content.Intent
import android.os.PowerManager
import android.widget.Toast
import androidx.annotation.StringRes
import cn.vove7.smartkey.android.AndroidSettings

/**
 * Created by 11324 on 2020/5/8
 */
class App : Application() {

    companion object {
        fun toast(msg: String, dur: Int = Toast.LENGTH_SHORT) {
            Toast.makeText(INS, msg, dur).show()
        }

        fun toast(@StringRes sId: Int, dur: Int = Toast.LENGTH_SHORT) {
            Toast.makeText(INS, sId, dur).show()
        }

        lateinit var INS: App

        val powerManager by lazy {
            INS.getSystemService(PowerManager::class.java)!!
        }
        val keyguardManager by lazy {
            INS.getSystemService(KeyguardManager::class.java)!!
        }
    }

    override fun onCreate() {
        INS = this
        AndroidSettings.init(this)
        super.onCreate()

    }

    override fun startActivity(intent: Intent?) {
        intent?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        super.startActivity(intent)
    }
}
