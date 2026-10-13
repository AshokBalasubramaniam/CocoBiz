package com.cocobiz.app.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.cocobiz.app.MainActivity
import com.cocobiz.app.R

object NotificationUtils {
    const val CHANNEL_ID_REMINDERS = "cocobiz_reminders"
    const val CHANNEL_ID_COMPLETION = "cocobiz_completion"
    const val CHANNEL_ID_DOWNLOADS = "cocobiz_downloads"

    fun createNotificationChannels(context: Context) {
        val reminderChannel = NotificationChannel(
            CHANNEL_ID_REMINDERS,
            "Sale Reminders",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Notifications for upcoming coconut sales"
        }

        val completionChannel = NotificationChannel(
            CHANNEL_ID_COMPLETION,
            "Sale Completion",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Notifications when sales are automatically completed"
        }

        val downloadsChannel = NotificationChannel(
            CHANNEL_ID_DOWNLOADS,
            "Downloads",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Notifications when a report finishes downloading"
        }

        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager?.createNotificationChannels(
            listOf(reminderChannel, completionChannel, downloadsChannel)
        )
    }

    fun showDownloadCompleteNotification(
        context: Context,
        notificationId: Int,
        fileName: String,
        fileUri: Uri?
    ) {
        val contentIntent = fileUri?.let { uri ->
            val viewIntent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            PendingIntent.getActivity(
                context, notificationId, viewIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_DOWNLOADS)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Report downloaded")
            .setContentText("$fileName saved to Downloads/CocoBiz")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .apply { if (contentIntent != null) setContentIntent(contentIntent) }
            .build()

        try {
            NotificationManagerCompat.from(context).notify(notificationId, notification)
        } catch (e: SecurityException) {
            // Permission not granted
        }
    }

    fun showReminderNotification(
        context: Context,
        notificationId: Int,
        dealerName: String,
        daysRemaining: Long
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, notificationId, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val title = "Coconut Sale Reminder"
        val message = when (daysRemaining) {
            0L -> "Coconut sale for $dealerName is due today!"
            1L -> "Coconut sale for $dealerName is due tomorrow!"
            else -> "Dealer $dealerName's coconut sale is due in $daysRemaining days."
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_REMINDERS)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(notificationId, notification)
        } catch (e: SecurityException) {
            // Permission not granted
        }
    }

    fun showCompletionNotification(
        context: Context,
        notificationId: Int,
        dealerName: String
    ) {
        val notification = NotificationCompat.Builder(context, CHANNEL_ID_COMPLETION)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Sale Completed")
            .setContentText("Sale for $dealerName has been automatically marked as completed.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(notificationId, notification)
        } catch (e: SecurityException) {
            // Permission not granted
        }
    }
}
