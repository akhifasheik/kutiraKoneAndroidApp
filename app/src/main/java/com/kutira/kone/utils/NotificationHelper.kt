package com.kutira.kone.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.kutira.kone.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun ensureChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val trades = NotificationChannel(
                CHANNEL_TRADES,
                "Trade updates",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val nearby = NotificationChannel(
                CHANNEL_NEARBY,
                "Nearby fabrics",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            manager.createNotificationChannel(trades)
            manager.createNotificationChannel(nearby)
        }
    }

    fun showTradeNotification(title: String, message: String) {
        val notification = NotificationCompat.Builder(context, CHANNEL_TRADES)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(context).notify(TRADE_NOTIF_BASE + title.hashCode(), notification)
    }

    fun showNearbyFabricNotification(title: String, message: String) {
        val notification = NotificationCompat.Builder(context, CHANNEL_NEARBY)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(context)
            .notify(NEARBY_NOTIF_BASE + message.hashCode(), notification)
    }

    companion object {
        const val CHANNEL_TRADES = "kutira_trades"
        const val CHANNEL_NEARBY = "kutira_nearby"
        private const val TRADE_NOTIF_BASE = 10_000
        private const val NEARBY_NOTIF_BASE = 20_000
    }
}
