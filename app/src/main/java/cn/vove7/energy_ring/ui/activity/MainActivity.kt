package cn.vove7.energy_ring.ui.activity

import android.app.Activity
import android.content.*
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ActionMenuView
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.Toast
import cn.vove7.energy_ring.R
import cn.vove7.energy_ring.model.ShapeType
import cn.vove7.energy_ring.service.AccService
import cn.vove7.energy_ring.ui.adapter.StylePagerAdapter
import cn.vove7.energy_ring.util.*
import cn.vove7.energy_ring.util.state.ApplicationState
import cn.vove7.energy_ring.util.state.Config
import cn.vove7.energy_ring.util.state.DevicePresets
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.checkbox.checkBoxPrompt
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.list.listItems
import kotlinx.android.synthetic.main.activity_main.*

const val CONFIG_JSON_CREATE = 1
const val CONFIG_JSON_LOAD = 2

class MainActivity : BaseActivity(), ActionMenuView.OnMenuItemClickListener {

    private val pageAdapter by lazy {
        StylePagerAdapter(supportFragmentManager)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        style_view_pager.adapter = pageAdapter

        enable_service.isChecked = ApplicationState.enabled
        enable_service.setOnClickListener(::enableServiceToggle)
        export_view.setOnClickListener(::storeToFile)
        import_view.setOnClickListener(::loadFromFile)
        initRadioStylesView()

        styleButtons[Config.INS.device.energyType.ordinal].callOnClick()

        menuInflater.inflate(R.menu.main, menu_view.menu)
        menu_view.setOnMenuItemClickListener(this)
        menu_view.overflowIcon = getDrawable(R.drawable.ic_settings)

        refreshMenu()
    }

    override fun onResume() {
        // Ensure ApplicationState.enabled matches rules that AccService has to be enabled
        // and the user has the toggle checked
        if (!AccService.enabled || !enable_service.isChecked) {
            enable_service.isChecked = false
            ApplicationState.enabled = false
        } else {
            ApplicationState.enabled = true
        }
        applyConfigAndRefreshMenu()
        super.onResume()
    }

    private fun initRadioStylesView() {
        button_style_ring.setOnClickListener(::onStyleButtonClick)
        button_style_double_ring.setOnClickListener(::onStyleButtonClick)
        button_style_pill.setOnClickListener(::onStyleButtonClick)
    }

    private val styleButtons by lazy {
        arrayOf(button_style_ring, button_style_double_ring, button_style_pill)
    }

    private fun onStyleButtonClick(v: View) {
        val i = styleButtons.indexOf(v)
        styleButtons.forEach { it.isSelected = (it == v) }
        val newStyle = ShapeType.values()[i]
        if (Config.INS.device.energyType != newStyle) {
            Config.INS.device.energyType = newStyle
            ApplicationState.applyConfig()
        }
        style_view_pager.currentItem = i
    }


    override fun onMenuItemClick(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_about -> { showAbout(); return true }
            R.id.menu_color_mode -> { pickColorMode(); return true }
            R.id.menu_model_preset -> { pickPreSet(); return true }
            R.id.menu_force_refresh -> null //do nothing, FloatRingWindow will be refreshed
            R.id.show_rotated -> {
                Config.INS.showRotated = !Config.INS.showRotated
                item.isChecked = Config.INS.showRotated
            }
            R.id.show_battery_saver -> {
                Config.INS.showBatterySaver = !Config.INS.showBatterySaver
                item.isChecked = Config.INS.showBatterySaver
            }
            R.id.hide_battery_aod -> {
                Config.INS.hideBatteryAod = !Config.INS.hideBatteryAod
                item.isChecked = Config.INS.hideBatteryAod
            }
            R.id.show_screen_off -> {
                Config.INS.showScreenOff = !Config.INS.showScreenOff
                item.isChecked = Config.INS.showScreenOff
            }
        }
        applyConfigAndRefreshMenu()
        return true
    }

    private fun pickColorMode() {
        if (Config.INS.device.energyType == ShapeType.PILL) {
            Toast.makeText(this, R.string.not_support_current_mode, Toast.LENGTH_SHORT).show()
            return
        }
        MaterialDialog(this).show {
            title(R.string.color_mode)
            listItems(R.array.modes_of_color) { _, i, _ ->
                Config.INS.colorMode = i
                applyConfigAndRefreshMenu()
            }
        }
    }

    override fun onBackPressed() {
        finishAndRemoveTask()
    }

    private fun pickPreSet() {
        MaterialDialog(this).show {
            val allDs = DevicePresets.defaults.toMutableList()
            title(R.string.model_preset)
            message(R.string.hint_preset_share)
            var ds = allDs.filter { it.buildModel == Build.MODEL }
            if (ds.isEmpty()) {
                ds = allDs
            }
            listItems(items = ds.map { it.buildModel }, waitForPositiveButton = false) { _, i, _ ->
                dismiss()
                applyConfigAndRefreshMenu(deviceData = ds[i])
            }
            checkBoxPrompt(R.string.display_only_this_model, isCheckedDefault = ds.size != allDs.size) { c ->
                val dss = if (c) allDs.filter { it.buildModel.equals(Build.MODEL, ignoreCase = true) }
                else allDs
                listItems(items = dss.map { it.buildModel }, waitForPositiveButton = false) { _, i, _ ->
                    applyConfigAndRefreshMenu(deviceData = dss[i])
                }
            }
            negativeButton(R.string.close)
        }
    }

    private fun applyConfigAndRefreshMenu(config: Config = Config.INS, deviceData: Config.DeviceData? = null) {
        if (deviceData != null) {
            config.device = deviceData
        }
        ApplicationState.applyConfig(config)
        refreshMenu()
    }

    private fun refreshMenu() {
        menu_view.menu.findItem(R.id.menu_color_mode).title = getString(R.string.color_mode) + ": " +
                resources.getStringArray(R.array.modes_of_color)[Config.INS.colorMode]
        menu_view.menu.findItem(R.id.show_rotated).isChecked = Config.INS.showRotated
        menu_view.menu.findItem(R.id.show_screen_off).isChecked = Config.INS.showScreenOff
        menu_view.menu.findItem(R.id.show_battery_saver).isChecked = Config.INS.showBatterySaver
        menu_view.menu.findItem(R.id.hide_battery_aod).isChecked = Config.INS.hideBatteryAod
        pageAdapter.getItem(style_view_pager.currentItem).onResume()
        styleButtons[Config.INS.device.energyType.ordinal].callOnClick()
    }

    private fun enableServiceToggle(view: View) {
        if (!AccService.enabled) {
            openAccessibilityPermission()
        } else {
            ApplicationState.enabled = !ApplicationState.enabled
            enable_service.isChecked = ApplicationState.enabled
            applyConfigAndRefreshMenu()
        }
    }

    private fun storeToFile(view: View) {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/json"
            putExtra(Intent.EXTRA_TITLE, "energy_ring_config.json")
        }
        startActivityForResult(intent, CONFIG_JSON_CREATE)
    }

    private fun loadFromFile(view: View) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/json"
        }
        startActivityForResult(intent, CONFIG_JSON_LOAD)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            resultData?.data?.also { uri ->
                when (requestCode) {
                    CONFIG_JSON_CREATE -> {
                        val writer = contentResolver.openOutputStream(uri)!!.bufferedWriter()
                        writer.write(Config.INS.jsonSerialize())
                        writer.close()
                    }
                    CONFIG_JSON_LOAD -> {
                        val reader = contentResolver.openInputStream(uri)!!.bufferedReader()
                        val configString = reader.lines().toArray().joinToString(separator = "\n", limit = 500)
                        reader.close()
                        try {
                            applyConfigAndRefreshMenu(Config.jsonDeserialize(configString))
                        } catch (e : Exception) {
                            Toast.makeText(this@MainActivity, R.string.parse_json_fail, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        } else {
            Toast.makeText(this@MainActivity, R.string.file_selection_fail, Toast.LENGTH_SHORT).show()
        }
        super.onActivityResult(requestCode, resultCode, resultData)
    }

    private fun showAbout() {
        MaterialDialog(this).show {
            title(R.string.about)
            message(R.string.about_msg)
            negativeButton(R.string.author) {
                openLink("https://coolapk.com/u/1090701")
            }
            neutralButton(text = "Github") {
                openLink("https://www.github.com/Vove7/EnergyRing")
            }
            positiveButton(R.string.support, click = ::donate)
        }
    }

    private fun openLink(link: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.data = Uri.parse(link)
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this@MainActivity, R.string.no_browser_available, Toast.LENGTH_SHORT).show()
        }
    }

    private fun donate(d: MaterialDialog) {
        MaterialDialog(this).show {
            title(R.string.way_support)
            listItems(R.array.way_of_support) { _, i, c ->
                when (i) {
                    0 -> {
                        if (DonateHelper.isInstallAlipay(this@MainActivity)) {
                            DonateHelper.openAliPay(this@MainActivity)
                        } else {
                            Toast.makeText(context, R.string.alipay_is_not_installed, Toast.LENGTH_SHORT).show()
                        }
                    }
                    1 -> {
                        showWxQr()
                    }
                    2 -> {
                        starGithubRepo()
                    }
                    //todo ad donate
                }
            }
        }
    }

    private fun starGithubRepo() {
        MaterialDialog(this).show {
            title(text = "Star Github 仓库以支持作者")
            message(text = "此方式您需要一个Github账号，若没有可使用邮箱注册；点击下面打开链接，点击Star按钮即可。")
            positiveButton(R.string.open_link) {
                try {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent.data = Uri.parse("https://github.com/Vove7/yyets_flutter")
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(this@MainActivity,
                            R.string.no_browser_available, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showWxQr() {
        MaterialDialog(this@MainActivity).show {
            title(R.string.hint_wx_donate)
            customView(view = ImageView(this@MainActivity).apply {
                adjustViewBounds = true
                setImageResource(R.drawable.qr_wx)
            })
        }
    }
}
