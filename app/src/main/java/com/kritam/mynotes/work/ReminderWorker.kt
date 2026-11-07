package com.kritam.mynotes.work

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class ReminderWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val noteTitle = inputData.getString("note_title") ?: "MyNotes Reminder"
        val noteMessage = inputData.getString("note_message") ?: "A task is due."
        val noteId = inputData.getInt("note_id", 0)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return Result.retry()
        }

        val channelId = "my_notes_reminders"
        val notificationManager = applicationContext.getSystemService(NotificationManager::class.java)

        if (notificationManager != null) {
            val channel = NotificationChannel(
                channelId,
                "MyNotes Reminders",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle(noteTitle)
            .setContentText(noteMessage)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager?.notify(noteId, notification)
        return Result.success()
    }
}
