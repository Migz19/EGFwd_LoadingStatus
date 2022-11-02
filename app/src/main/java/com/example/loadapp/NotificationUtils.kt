package com.example.loadapp

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat

val NOTIFICATION_ID = 111
fun NotificationManager.sendNotification(messageBody: String, context: Context) {
    val contentIntent = Intent(context, DetailActivity::class.java)
    val contentPendingIntent = PendingIntent.getActivity(
        context,
        NOTIFICATION_ID,
        contentIntent,
        PendingIntent.FLAG_IMMUTABLE
    )
    val builder = NotificationCompat.Builder(
        context,
        context.getString(R.string.channel_id))
        .setSmallIcon(R.drawable.notification_icon)
        .setContentTitle(context.getString(R.string.notification_title))
        .setContentText(messageBody)
        .setContentIntent(contentPendingIntent)
        .setAutoCancel(true)
    notify(NOTIFICATION_ID, builder.build())
}

fun NotificationManager.cancelNotifications() {
    cancelAll()
}