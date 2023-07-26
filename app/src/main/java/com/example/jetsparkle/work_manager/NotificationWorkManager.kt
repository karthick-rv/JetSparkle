package com.example.jetsparkle.work_manager

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters

class NotificationWorkManager(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    private val CHANNEL_ID = "MainActivity"

    private val notification = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(androidx.core.R.drawable.notification_bg_normal)
        .setContentTitle("Hello World")
        .setContentText("This is your test notification")
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .build()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun doWork(): Result {
        createNotificationChannel()
        with(NotificationManagerCompat.from(applicationContext)) {
            notify(1, notification)
        }
        return Result.success()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Test Notification",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "This is your MainActivity's test channel"
        }
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}