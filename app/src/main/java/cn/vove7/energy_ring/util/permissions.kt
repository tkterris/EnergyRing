package cn.vove7.energy_ring.util

import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import android.os.Bundle
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

fun openFloatPermission() {
    Toast.makeText(App.INS, R.string.request_float_window_permission, Toast.LENGTH_SHORT).show()
    val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:" + App.INS.packageName))
    intent.putComponent(App::class.java)
    App.INS.startActivity(intent)

}

private fun Intent.putComponent(cls: Class<*>) {
    val cs = ComponentName(App.INS.packageName, cls.name).flattenToString()
    val bundle = Bundle()
    bundle.putString(":settings:fragment_args_key", cs)
    putExtra(":settings:fragment_args_key", cs)
    putExtra(":settings:show_fragment_args", bundle)
}
