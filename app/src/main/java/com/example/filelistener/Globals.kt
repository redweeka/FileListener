package com.example.filelistener

import java.io.File

class Globals {
    companion object {
        // File system related
        var OBSERVED_FOLDER_PATH = File("sdcard/DCIM/Camera")
        const val DEFAULT_PHOTO_SENT_FOLDER_NAME = "temp"

        // Api related
        const val API_PHOTOS_FILE_URL = "http://10.0.0.8:5000/upload" //Insert here your server ipv4 address.
    }
}