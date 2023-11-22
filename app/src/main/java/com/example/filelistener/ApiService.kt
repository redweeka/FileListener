package com.example.filelistener

import android.util.Log
import com.example.filelistener.Globals.Companion.API_PHOTOS_FILE_URL
import com.example.filelistener.Globals.Companion.DEFAULT_PHOTO_SENT_FOLDER_NAME
import java.io.DataOutputStream
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLConnection
import java.nio.file.Files
import java.nio.file.StandardCopyOption

class ApiService {
    companion object {
        private const val TAG = "ApiService"

        fun sendFileToApi(file: File) {
            val boundary = "~~~~~~" // Any random string can be a boundary
            val lineEnd = "\r\n"
            val twoHyphens = "--"

            try {
                val url = URL(API_PHOTOS_FILE_URL)

                val connection = (url.openConnection() as HttpURLConnection).apply {
                    // Set connection properties
                    doInput = true
                    doOutput = true
                    useCaches = false
                    requestMethod = "POST"
                    setRequestProperty("Connection", "Keep-Alive")
                    setRequestProperty("Content-Type", "multipart/form-data; boundary=$boundary")
                }

                DataOutputStream(connection.outputStream).use { outputStream ->
                    // Add file parameter
                    outputStream.writeBytes("$twoHyphens$boundary$lineEnd")
                    outputStream.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\"${file.name}\"$lineEnd")
                    outputStream.writeBytes("Content-Type: ${URLConnection.guessContentTypeFromName(file.name)}$lineEnd")
                    outputStream.writeBytes(lineEnd)

                    // Write file data
                    val fileBytes = Files.readAllBytes(file.toPath())
                    outputStream.write(fileBytes)

                    // End the file
                    outputStream.writeBytes(lineEnd)
                    outputStream.writeBytes("$twoHyphens$boundary$twoHyphens$lineEnd")
                    outputStream.flush()
                }

                // If response succeed move the file to sent folder
                if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                    Log.d(TAG, "sendFileToApi: send file ${file.name} successfully")
                    movePhotoToSentFolder(file)
                } else {
                    Log.e(TAG, "sendFileToApi: send file ${file.name} failed")
                }
            } catch (e: Exception) {
                Log.e(TAG, "sendFileToApi: ", e)
            }
        }

        private fun createDirectoryIfNotExists(directory: File): Boolean =
            if (!directory.exists()) {
                directory.mkdirs()
            } else {
                true
            }

        private fun movePhotoToSentFolder(file: File) {
            val destinationParentFile = File(Globals.OBSERVED_FOLDER_PATH).parentFile
            val tempFolder = File("$destinationParentFile/$DEFAULT_PHOTO_SENT_FOLDER_NAME")
            val destFile = File("$tempFolder/${file.name}")

            if (createDirectoryIfNotExists(tempFolder)) {
                try {
                    // Move the file content to the destination
                    Files.move(
                        file.toPath(),
                        destFile.toPath(),
                        StandardCopyOption.REPLACE_EXISTING
                    )
                } catch (e: Exception) {
                    // Handle the exception
                    Log.e(TAG, "Failed to move file: ${file.absolutePath}", e)
                }
            } else {
                Log.e(TAG, "movePhotoToSentFolder: cant create sent folder")
            }
        }
    }
}