package com.example.filelistener

import android.util.Log
import java.io.File
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class FileScheduler(private val observedFolder: File) {
    private val TAG = "MyFileScheduler"
    private val scheduler = Executors.newSingleThreadScheduledExecutor()
    private var isScheduled = false

    private fun sendPhotos() {
        Log.d(TAG, "sendPhotos: search for new photos")

        observedFolder.listFiles()?.forEach { currentFile ->
            if (currentFile.isFile) {
                Log.i(TAG, "sendPhotos: $currentFile")
                ApiService.sendFileToApi(currentFile)
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