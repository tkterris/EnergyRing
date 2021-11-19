package cn.vove7.energy_ring.util

import android.content.Intent
import android.provider.Settings
import android.widget.Toast
import cn.vove7.energy_ring.App
import cn.vove7.energy_ring.R


/**
 * # permissions
 *
 * @author Vove
 * 2020/5/21
 */

fun openAccessibilityPermission() {
    Toast.makeText(App.INS, R.string.request_float_window_permission, Toast.LENGTH_SHORT).show()
    val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
    App.INS.startActivity(intent)

}
