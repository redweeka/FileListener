package com.example.filelistener

import android.util.Log
import java.io.File
import java.nio.file.Files
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class MyFileScheduler(private val folderPath: String) {
    private val TAG = "MyFileScheduler"
    private val scheduler = Executors.newSingleThreadScheduledExecutor()
    private var isScheduled = false

    private fun sendPhotos() {
        Log.d(TAG, "sendPhotos: search for new photos")

        Files.walk(File(folderPath).toPath())
            .forEach { file ->
                if (Files.isRegularFile(file)) {
                    Log.i(TAG, "sendPhotos: $file")
                    ApiService.sendFileToApi(file.toFile())
                }
            }
    }

    fun startWatching() {
        if (!isScheduled) {
            Log.d(TAG, "startWatching: ")
            scheduler.scheduleAtFixedRate({ sendPhotos() }, 0, 7, TimeUnit.SECONDS)
            isScheduled = true
        } else {
            Log.d(TAG, "startWatching: already watching")
        }
    }

    fun stopWatching() {
        Log.d(TAG, "stopWatching: ")
        scheduler.shutdown()
        isScheduled = false
    }
}