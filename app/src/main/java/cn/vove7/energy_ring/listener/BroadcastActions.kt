package cn.vove7.energy_ring.listener

import cn.vove7.energy_ring.App

object BroadcastActions {
    private val ACTION_PREFIX = App.INS.packageName + "."
    val DISPLAY_UPDATE = ACTION_PREFIX + "DISPLAY_UPDATE"
    val DISPLAY_REFRESH = ACTION_PREFIX + "DISPLAY_REFRESH"
}