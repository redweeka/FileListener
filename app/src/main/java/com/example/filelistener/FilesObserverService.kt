package com.example.filelistener

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.provider.MediaStore
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.filelistener.Globals.Companion.OBSERVED_FOLDER_PATH
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import java.io.File

class FilesObserverService : Service() {
    private val TAG = "FilesObserverService"
    private val scope = CoroutineScope(Dispatchers.Default)
    private var fileObserver: MyFileScheduler? = null

    companion object {
        const val CHANNEL_ID = "UploadServiceChannel"
        const val NOTIFICATION_ID = 1
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand: 899")
        startForeground(NOTIFICATION_ID, createNotification())
        initCameraDirectoryPath()
        startFileObserver()

        return START_STICKY
    }

    private fun startFileObserver() {
        if (File(OBSERVED_FOLDER_PATH).exists()) {
            fileObserver = MyFileScheduler(OBSERVED_FOLDER_PATH)
            fileObserver?.startWatching()
        } else {
            Log.e(TAG, "accessFolder: photo folder not found")
        }
    }

    private fun initCameraDirectoryPath() {
        val projection = arrayOf(MediaStore.Images.Media.DATA)

        contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            "${MediaStore.Images.Media.DATE_TAKEN} DESC"
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)

                File(cursor.getString(columnIndex)).parent?.let { cameraDirectory ->
                    Log.d(TAG, "getCameraDirectoryPath: cameraDirectory - $cameraDirectory")

                    // Default directory if no image directory is found (modify as needed)
                    OBSERVED_FOLDER_PATH = cameraDirectory
                }
            }
        }
    }

    private fun createNotification(): Notification {
        // Create a notification channel (required for Android 8.0 and above)
        createNotificationChannel()

        // Create a notification intent (you can customize this based on your app's requirements)
        val notificationIntent = Intent(this, FilesObserverService::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Build the notification
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Uploading Service")
            .setContentText("Service is running")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .build()
    }

    private fun createNotificationChannel() {
        val name = "UploadServiceChannel"
        val descriptionText = "Channel for Upload Service"
        val channel = NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_LOW).apply {
                description = descriptionText
        }

        // Register the channel with the system
        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.createNotificationChannel(channel)
    }

    override fun onDestroy() {
        super.onDestroy()
        fileObserver?.stopWatching()
        scope.cancel()
    }
}