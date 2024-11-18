package com.example.devems

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val eventId = intent.getLongExtra("eventId", -1)
        val eventName = intent.getStringExtra("eventName") ?: "Event Reminder"
        val channelId = "event_channel_id"

        Log.d("NotificationReceiver", "Received broadcast for eventId: $eventId, eventName: $eventName")

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Event Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Channel for event reminders"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notificationIntent = Intent(context, DashboardActivity::class.java).apply {
            putExtra("eventId", eventId)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            eventId.toInt(),
            notificationIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.app_profile)
            .setContentTitle("Event Reminder")
            .setContentText("Event Started: $eventName")
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        notificationManager.notify(eventId.toInt(), notification)
    }
}
