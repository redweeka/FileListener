package com.example.filelistener

import android.os.Build
import android.os.FileObserver
import android.os.FileObserver.ALL_EVENTS
import android.os.FileObserver.CLOSE_WRITE
import android.util.Log
import java.io.File

class MyFileObserver(val folderPath: String) {
    private val TAG = "MtFileObserver"
    private var fileObserver: FileObserver? = null

    init {
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
    }

    fun onEvent(event: Int, fileRelativePath: String?) {
        when (event and ALL_EVENTS) {
            CLOSE_WRITE -> fileRelativePath?.let {
                Log.d(TAG, "File created/updated: $it")
                // Handle the created file here
                val newFile = File("$folderPath/${it.split("-").last()}")

                // TODO: camera save picture and then change its name, need to handle it better
//                if (newFile.exists()) {
                ApiService.sendFileToApi(newFile)
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
        fileObserver?.stopWatching()
    }
}
