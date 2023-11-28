package com.example.filelistener

import android.util.Log
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.Response
import java.io.File
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.util.LinkedList
import java.util.Queue
import java.util.concurrent.TimeUnit

class ApiService private constructor() {
    private val TAG = "ApiService"
    private val configManager = ConfigManager.getInstance()
    private val client = OkHttpClient.Builder().apply {
        val period = configManager.getServiceSecondsPeriod()
        readTimeout(period, TimeUnit.SECONDS)
        writeTimeout(period, TimeUnit.SECONDS)
    }.build()
    val pendingFilesQueue: Queue<File> = LinkedList()

    fun sendFileToApi(filesQueue: Queue<File>) {
        filesQueue.forEach { currentFile ->
            val imageMediaType = "image/${currentFile.extension}".toMediaType()

            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(
                    "file",
                    currentFile.name,
                    currentFile.asRequestBody(imageMediaType)
                )
                .build()

            val url = configManager.getUrl()
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            pendingFilesQueue.offer(currentFile)

            client.newCall(request).enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        Log.d(TAG, "sendFileToApi: send file $currentFile successfully")
                        movePhotoToSentFolder(currentFile)
                    } else {
                        Log.e(TAG, "sendFileToApi: send files with code ${response.code}")
                    }

                    pendingFilesQueue.remove(currentFile)
                }

                override fun onFailure(call: Call, e: IOException) {
                    when (e) {
                        is SocketTimeoutException -> Log.e(TAG, "onFailure: url $url not up")
                        is ConnectException -> Log.e(TAG, "onFailure: server $url is offline")
                        else -> Log.e(TAG, "onFailure: connection failed: ", e)
                    }

                    pendingFilesQueue.remove(currentFile)
                }
            })
        }
    }

    private fun createDirectoryIfNotExists(directory: File): Boolean =
        if (!directory.exists()) {
            directory.mkdirs()
        } else {
            true
        }

    private fun movePhotoToSentFolder(file: File) {
        val destinationParentFile = file.parentFile?.parent
        val sentFolderName = configManager.getSentFolderName()
        val sentFolder = File("$destinationParentFile/$sentFolderName")
        val destFile = File("$sentFolder/${file.name}")

        if (createDirectoryIfNotExists(sentFolder)) {
            try {
                // Move the file content to the destination
                Files.move(
                    file.toPath(),
                    destFile.toPath(),
                    StandardCopyOption.REPLACE_EXISTING,
                    StandardCopyOption.ATOMIC_MOVE
                )
            } catch (e: Exception) {
                // Handle the exception
                Log.e(TAG, "Failed to move file: ${file.absolutePath}", e)
            }
        } else {
            Log.e(TAG, "movePhotoToSentFolder: cant create sent folder")
        }
    }

    companion object {
        @Volatile
        private var instance: ApiService? = null

        fun getInstance(): ApiService {
            if (instance == null) {
                synchronized(this) {
                    if (instance == null) {
                        instance = ApiService()
                    }
                }
            }

            return instance!!
        }
    }
}