package com.example.filelistener

import android.util.Log
import com.example.filelistener.Globals.Companion.IMAGE_FILES_EXTENSIONS
import java.io.File
import java.util.LinkedList
import java.util.Queue
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class FileScheduler {
    private val TAG = "MyFileScheduler"
    private val scheduler = Executors.newSingleThreadScheduledExecutor()
    private var isScheduled = false
    private val configManager = ConfigManager.getInstance()

    private fun sendPhotos() {
        Log.d(TAG, "sendPhotos: search for new photos")
        val filesQueue: Queue<File> = LinkedList()

        configManager.getObservedFolders().forEach { observedFolder ->
            if (observedFolder.exists()) {
                observedFolder.listFiles()?.forEach { currentFile ->
                    val isFilePending = ApiService.getInstance().pendingFilesQueue.contains(currentFile)
                    val isImageFile = IMAGE_FILES_EXTENSIONS.contains(currentFile.extension.lowercase())

                    if (currentFile.isFile && isImageFile && !isFilePending) {
                        Log.i(TAG, "sendPhotos: add photo $currentFile")
                        filesQueue.offer(currentFile)
                    }
                }
            }
        }

        ApiService.getInstance().sendFileToApi(filesQueue)
    }

    fun startWatching() {
        if (!isScheduled) {
            Log.d(TAG, "startWatching: ")
            val period = configManager.getServiceSecondsPeriod()
            scheduler.scheduleAtFixedRate({ sendPhotos() }, 0, period, TimeUnit.SECONDS)
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