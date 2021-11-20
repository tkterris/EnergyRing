package cn.vove7.energy_ring.ui.activity

import android.content.*
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ActionMenuView
import android.widget.ImageView
import android.widget.Toast
import cn.vove7.energy_ring.App
import cn.vove7.energy_ring.R
import cn.vove7.energy_ring.floatwindow.FloatRingWindow
import cn.vove7.energy_ring.model.ShapeType
import cn.vove7.energy_ring.service.AccService
import cn.vove7.energy_ring.ui.adapter.StylePagerAdapter
import cn.vove7.energy_ring.util.*
import cn.vove7.energy_ring.util.state.ApplicationState
import cn.vove7.energy_ring.util.state.Config
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.checkbox.checkBoxPrompt
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.input.input
import com.afollestad.materialdialogs.list.listItems
import com.afollestad.materialdialogs.list.listItemsMultiChoice
import com.google.gson.JsonSyntaxException
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity(), ActionMenuView.OnMenuItemClickListener {

    private val pageAdapter by lazy {
        StylePagerAdapter(supportFragmentManager)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        style_view_pager.adapter = pageAdapter

        view_info_view.setOnClickListener(::outConfig)
        import_view.setOnClickListener(::importFromClip)
        initRadioStylesView()

        styleButtons[Config.INS.energyType.ordinal].callOnClick()

        menuInflater.inflate(R.menu.main, menu_view.menu)
        menu_view.setOnMenuItemClickListener(this)
        menu_view.overflowIcon = getDrawable(R.drawable.ic_settings)

        refreshData()

        if (!AccService.enabled) {
            openAccessibilityPermission()
        }
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
        if (Config.INS.energyType != newStyle) {
            Config.INS.energyType = newStyle
            FloatRingWindow.update(layoutChange = true)
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
            R.id.show_screen_off -> {
                Config.INS.showScreenOff = !Config.INS.showScreenOff
                item.isChecked = Config.INS.showScreenOff
            }
        }
        refreshData()
        FloatRingWindow.update(layoutChange = true)
        return true
    }

    private fun pickColorMode() {
        if (Config.INS.energyType == ShapeType.PILL) {
            Toast.makeText(this, R.string.not_support_current_mode, Toast.LENGTH_SHORT).show()
            return
        }
        MaterialDialog(this).show {
            title(R.string.color_mode)
            listItems(R.array.modes_of_color) { _, i, _ ->
                Config.INS.colorMode = i
                refreshData()
                FloatRingWindow.update(layoutChange = true)
            }
        }
    }

    private fun outConfig(view: View) {
        val msg = Config.jsonSerialize(Config.INS)
        MaterialDialog(this).show {
            title(R.string.config_data)
            message(text = "$msg\n" + getString(R.string.welcome_to_share_on_comment_area))
            positiveButton(R.string.copy) {
                val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                cm.setPrimaryClip(ClipData.newPlainText("EnergyRing", msg))
            }
            negativeButton(R.string.save_current_config) {
                saveConfig(Config.INS)
            }
        }
    }

    private fun saveConfig(config: Config, name: CharSequence = Build.MODEL) {
        MaterialDialog(this@MainActivity).show {
            title(R.string.config_title)
            input(waitForPositiveButton = true, prefill = name) { _, s ->
                config.buildModel = s.toString()
                ApplicationState.saveConfig(config)
            }
            positiveButton()
            negativeButton()
        }
    }

    override fun onBackPressed() {
        finishAndRemoveTask()
    }

    private fun pickPreSet() {
        MaterialDialog(this).show {
            val allDs = Config.presetDevices.toMutableList().also {
                it.addAll(ApplicationState.savedConfigs)
            }
            title(R.string.model_preset)
            message(R.string.hint_preset_share)
            var ds = allDs.filter { it.buildModel == Build.MODEL }
            if (ds.isEmpty()) {
                ds = allDs
            }
            listItems(items = ds.map { it.buildModel }, waitForPositiveButton = false) { _, i, _ ->
                dismiss()
                applyConfig(ds[i])
            }
            checkBoxPrompt(R.string.display_only_this_model, isCheckedDefault = ds.size != allDs.size) { c ->
                val dss = if (c) allDs.filter { it.buildModel.equals(Build.MODEL, ignoreCase = true) }
                else allDs
                listItems(items = dss.map { it.buildModel }, waitForPositiveButton = false) { _, i, _ ->
                    applyConfig(dss[i])
                }
            }
            positiveButton(R.string.edit) { editLocalConfig() }
            negativeButton(R.string.close)
        }
    }

    private fun editLocalConfig() {
        MaterialDialog(this).show {
            title(R.string.edit_local_config)
            listItemsMultiChoice(items = ApplicationState.savedConfigs.map { it.buildModel }, waitForPositiveButton = true) { _, indices, _ ->
                val savedConfigs = ApplicationState.savedConfigs.toMutableList()
                savedConfigs.removeAll(indices.map { savedConfigs[it] })
                ApplicationState.savedConfigs = savedConfigs.toTypedArray()
            }
            positiveButton(R.string.delete_selected)
            negativeButton()
        }
    }

    private fun applyConfig(config: Config) {
        ApplicationState.applyConfig(config)
        FloatRingWindow.update(layoutChange = true)
        refreshData()
    }

    private fun refreshData() {
        menu_view.menu.findItem(R.id.menu_color_mode).title = getString(R.string.color_mode) + ": " +
                resources.getStringArray(R.array.modes_of_color)[Config.INS.colorMode]
        menu_view.menu.findItem(R.id.show_rotated).isChecked = Config.INS.showRotated
        menu_view.menu.findItem(R.id.show_screen_off).isChecked = Config.INS.showScreenOff
        menu_view.menu.findItem(R.id.show_battery_saver).isChecked = Config.INS.showBatterySaver
        pageAdapter.getItem(style_view_pager.currentItem).onResume()
        styleButtons[Config.INS.energyType.ordinal].callOnClick()
        ApplicationState.persistState()
    }

    private fun importFromClip(view: View) {
        val content = getSystemService(ClipboardManager::class.java)!!.primaryClip?.let {
            it.getItemAt(it.itemCount - 1).text
        }
        if (content == null) {
            Toast.makeText(this, R.string.empty_in_clipboard, Toast.LENGTH_SHORT).show()
            return
        }
        MaterialDialog(this).show {
            title(R.string.clipboard_content)
            message(text = content)
            positiveButton(R.string.text_import) {
                importConfig(content.toString(), false)
            }
            negativeButton(R.string.config_import_and_save) {
                importConfig(content.toString(), true)
            }
        }
    }

    private fun importConfig(content: String, save: Boolean) {
        kotlin.runCatching {
            Config.jsonDeserialize(content)
        }.onSuccess {
            applyConfig(it)
            if (save) {
                saveConfig(it)
            }
        }.onFailure {
            if (it is JsonSyntaxException) {
                App.toast(R.string.import_config_hint, Toast.LENGTH_LONG)
            } else {
                App.toast(it.message ?: getString(R.string.import_failed))
            }
        }
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
