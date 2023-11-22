package com.example.filelistener

import android.util.Log
import com.example.filelistener.Constants.Companion.API_PHOTOS_FILE_URL
import java.io.DataOutputStream
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLConnection
import java.nio.file.Files

class ApiService {
    companion object {
        private const val TAG = "ApiService"

        fun sendFileToApi(file: File) {
            val boundary = "~~~~~~" // Any random string can be a boundary
            val lineEnd = "\r\n"
            val twoHyphens = "--"

            try {
                val url = URL(API_PHOTOS_FILE_URL)
                createDirectoryIfNotExists(url.toString())
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

                // Process if the response failed
                if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                    Log.e(TAG, "sendFileToApi: send file ${file.name} failed")
                } else {
                    Log.d(TAG, "sendFileToApi: send file ${file.name} successfully")
                }
            } catch (e: Exception) {
                e.printStackTrace()

                // Handle the exception
            }
        }


        private fun createDirectoryIfNotExists(directoryPath: String): Boolean {
            val directory = File(directoryPath)

            if (!directory.exists()) {
                return directory.mkdirs()
            }

            return true
        }



    }
}