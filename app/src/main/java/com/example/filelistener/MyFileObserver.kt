package com.example.filelistener

import android.os.Build
import android.os.FileObserver
import android.os.FileObserver.ALL_EVENTS
import android.os.FileObserver.CLOSE_WRITE
import android.os.Handler
import android.os.Looper
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.util.Log
import com.example.filelistener.Constants.Companion.FILE_OBSERVED_PATH
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption



class MyFileObserver(val folderPath: String) {
    private val TAG = "MyFileObserver"
    private var fileObserver: FileObserver? = null
    private var handler: Handler? = null
    private val photoList = mutableListOf<File>()

    init {
        //
        val gfgPolicy = ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(gfgPolicy)
        //
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            fileObserver = object : FileObserver(File(folderPath), ALL_EVENTS) {
                override fun onEvent(event: Int, path: String?) {
                    this@MyFileObserver.onEvent(event, path)
                }
            }
        } else {
            @Suppress("DEPRECATION")
            fileObserver = object : FileObserver(folderPath, ALL_EVENTS) {
                override fun onEvent(event: Int, path: String?) {
                    this@MyFileObserver.onEvent(event, path)
                }
            }
        }

        // Initialize the handler for periodic sending and moving
        handler = Handler(Looper.getMainLooper()) {
            sendPhotos()
            true
        }

        // Schedule the handler to run every 7 seconds
        handler?.sendEmptyMessageDelayed(0, 7000)
    }

    private fun sendPhotos() {
        if (photoList.isNotEmpty()) {
            // Move photos to "temp" folder
            movePhotosToTempFolder()

            // Clear the list after sending and moving
            photoList.clear()
        }

        // Schedule the handler to run again after 7 seconds
        handler?.sendEmptyMessageDelayed(0, 7000)
    }

    private fun movePhotosToTempFolder() {
        val dcimParentFile = File(FILE_OBSERVED_PATH).parentFile
        val tempFolder = File("$dcimParentFile/temp")
        if (!tempFolder.exists()) {
            tempFolder.mkdirs()
        }

        for (file in photoList) {
            val destFile = File("$tempFolder/${file.name}")

            try {
                // Copy the file content to the destination
                Files.copy(file.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING)

                // Delete the source file
                Files.delete(file.toPath())

                // File successfully moved, remove it from photoList
                //photoList.remove(file)
            } catch (e: Exception) {
                // Handle the exception
                e.printStackTrace()
                Log.e(TAG, "Failed to move file: ${file.absolutePath}")
            }
        }
        photoList.clear()
    }


    fun onEvent(event: Int, fileRelativePath: String?) {
        when (event and ALL_EVENTS) {
            CLOSE_WRITE -> fileRelativePath?.let {
                Log.d(TAG, "File created/updated: $it")
                // Handle the created file here
                val newFile = File("$folderPath/${it.split("-").last()}")
                photoList.add(newFile)
                // TODO: camera save picture and then change its name, need to handle it better
//                if (newFile.exists()) {
                ApiService.sendFileToApi(newFile)
//                photoList.add(File(API_PHOTOS_FILE_URL + "/" + newFile.name))
//                } else {
//                    Log.d(TAG, "onEvent: file not exist")
//                }
            }
        }
    }

    fun startWatching() {
        fileObserver?.startWatching()
    }

    fun stopWatching() {
        handler?.removeCallbacksAndMessages(null)

        fileObserver?.stopWatching()
    }
}
