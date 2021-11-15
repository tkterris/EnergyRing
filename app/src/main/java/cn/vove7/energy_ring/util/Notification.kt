package cn.vove7.energy_ring.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import cn.vove7.energy_ring.R
import cn.vove7.energy_ring.service.ForegroundService

/**
 * # Notifications
 *
 * @author tkterris
 * 2021-11-13
 */

const val FOREGROUND_NOTIFICATION_ID = 1000
private const val DESTROY_NOTIFICATION_ID = 1001

fun getAndShowForeNotification(context: Context): Notification {
    val notificationManager = context.getSystemService(NotificationManager::class.java)
    notificationManager.cancel(DESTROY_NOTIFICATION_ID)

    val notification = getForegroundNotification(context)

    notificationManager.notify(FOREGROUND_NOTIFICATION_ID, notification)
    return notification
}

fun getAndShowServiceDestroyedNotification (context: Context): Notification {
    val notificationManager = context.getSystemService(NotificationManager::class.java)
    val notification = getDestroyNotification(context)
    notificationManager.notify(DESTROY_NOTIFICATION_ID, notification)
    return notification
}

private fun getForegroundNotification(context: Context): Notification {
    return getBuilder(context, foregroundChannel).apply {
//            addAction(0, "唤醒".spanColor(googleBlue), getPendingIntent(VoiceAssistActivity.WAKE_UP))
//            addAction(0, "屏幕助手".spanColor(googleBlue), PendingIntent.getActivity(this@ForegroundService, 0, ScreenAssistActivity.createIntent(delayCapture = true), 0))
        priority = NotificationCompat.PRIORITY_MIN
        setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        setSmallIcon(R.mipmap.ic_launcher)
        setOngoing(true)
        setContentTitle(context.getString(R.string.foreground_service_title))
    }.build()
}

private val foregroundChannel
    get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        NotificationChannel("foreground_service", "前台服务", NotificationManager.IMPORTANCE_MIN).apply {
            setShowBadge(false)
            setSound(null, null)
            enableVibration(false)
            enableLights(false)
        }
    } else null

private fun getDestroyNotification(context: Context): Notification {
    //Create intent to launch foreground service
    val foregroundServiceIntent = Intent(context, ForegroundService::class.java)
    val pendingForegroundServiceIntent = PendingIntent.getService(context, 0, foregroundServiceIntent, PendingIntent.FLAG_IMMUTABLE)

    return getBuilder(context, destroyedChannel).apply {
        priority = NotificationCompat.PRIORITY_MIN
        setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        setSmallIcon(R.mipmap.ic_launcher)
        setOngoing(false)
        setContentTitle(context.getString(R.string.destroyed_title))
        setContentIntent(pendingForegroundServiceIntent)
        setAutoCancel(true)
    }.build()
}

private val destroyedChannel
    get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        NotificationChannel("destroyed_service", "Ring Destroyed", NotificationManager.IMPORTANCE_MIN).apply {
            setShowBadge(false)
            setSound(null, null)
            enableVibration(false)
            enableLights(false)
        }
    } else null

private fun getBuilder(context: Context, channel: NotificationChannel?) : NotificationCompat.Builder {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val c = channel!!
        context.getSystemService(NotificationManager::class.java)!!.createNotificationChannel(c)
        NotificationCompat.Builder(context, c.id)
    } else {
        NotificationCompat.Builder(context)
    }
}
