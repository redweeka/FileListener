package com.example.filelistener

import android.os.Environment
import java.io.File

class Globals {
    companion object {
        // File system related
        private val EXTERNAL_STORAGE = Environment.getExternalStorageDirectory() ?: File("/sdcard")
        val INI_FILE_PATH = "$EXTERNAL_STORAGE/FileListener/config.json"
        val CAMERA_FOLDER_PATH = File("$EXTERNAL_STORAGE/DCIM/Camera")
        private val PICTURES_FOLDER: File = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val OBSERVED_FOLDERS_PARENT = PICTURES_FOLDER
        val IMAGE_FILES_EXTENSIONS = listOf("jpeg", "jpg", "png")
        val DEFAULT_OBSERVED_FILES = listOf("אמלח", "תשתית", "תיעוד פנים", "איתור אויב", "איתור לוגיסטי", "כללי")
        const val DEFAULT_PHOTO_SENT_FOLDER_NAME = "sent"

        // Api related
        const val DEFAULT_UPLOAD_PHOTOS_URL = "http://10.0.0.20:3000/upload"

        // Service loop schedule
        const val DEFAULT_SERVICE_SECONDS_PERIOD = 30L
    }
}